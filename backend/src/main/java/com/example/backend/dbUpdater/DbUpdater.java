package com.example.backend.dbUpdater;

import com.example.backend.dbServices.ArticleDbService;
import com.example.backend.dbServices.FeatureDbService;
import com.example.backend.entity.ArticleEntity;
import com.example.backend.entity.FeatureEntity;
import com.example.backend.exceptions.HuggingFaceApiException;
import com.example.backend.exceptions.ProcessingException;
import com.example.backend.mapboxGeocodingService.Feature;
import com.example.backend.mapboxGeocodingService.GeoJson;
import com.example.backend.mapboxGeocodingService.MapboxGeocodingService;
import com.example.backend.processor.Processor;
import com.example.backend.sentimentAnalysisService.SentimentAnalysisResponseScore;
import com.example.backend.sentimentAnalysisService.SentimentAnalysisService;
import edu.stanford.nlp.simple.Sentence;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DbUpdater {

  private static final Logger LOGGER = LoggerFactory.getLogger(DbUpdater.class);

  private final FeatureDbService featureDbService;
  private final SentimentAnalysisService sentimentAnalysisService;
  private final MapboxGeocodingService mapboxGeocodingService;

  private final ArticleDbService articleDbService;

  private final Map<String, String> nationalityToCountryMap;

  @Autowired
  public DbUpdater(
      FeatureDbService featureDbService,
      SentimentAnalysisService sentimentAnalysisService,
      MapboxGeocodingService mapboxGeocodingService,
      ArticleDbService articleDbService
  ) throws IOException{
    this.sentimentAnalysisService = sentimentAnalysisService;
    this.mapboxGeocodingService = mapboxGeocodingService;
    this.featureDbService = featureDbService;
    this.articleDbService = articleDbService;
    this.nationalityToCountryMap = new HashMap<>();
    loadNationalityToCountryMap("backend/src/main/java/com/example/backend/processor/data/countries.csv");
  }

  private void loadNationalityToCountryMap(String filePath) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(filePath));
    String line;
    while ((line = br.readLine()) != null) {
      String[] values = line.split(",");
      if (values.length >= 4) {
        String nationality = values[3];
        String countryName = values[1];
        nationalityToCountryMap.put(nationality, countryName);
      }
    }
  }

  @RabbitListener(queues = {"${rabbitmq.queue.name}"})
  public void receiveProcessorMessage(String articleIdStr){
    ObjectId articleId = new ObjectId(articleIdStr);
    LOGGER.info("Message received -> " + articleId);

    Optional<ArticleEntity> articleEntity = articleDbService.findById(articleId);
    if (articleEntity.isPresent()){
      ArticleEntity article = articleEntity.get();
      processArticle(article);
    } else {
      LOGGER.info("Article ID " + articleId + " not found.");
    }
  }

  public void processArticle(ArticleEntity article){
    try {
      String headline = article.getWebTitle();
      System.out.println(headline);
      List<String> locations = getEntities(headline);
      if (locations.size() == 0) { // no locations, remove article from DB
        articleDbService.deleteById(article.get_id());
        LOGGER.info("No associated locations found for article "+ article.get_id() + ". Deleting from DB...");
        return;
      }
      int addedFeatures = 0;
      for (String location: locations){
        GeoJson geoJson = mapboxGeocodingService.getFeatureForLocation(location);
        System.out.println("GeoJson: " + geoJson);
        FeatureEntity feature = convertFeatureToDbEntity(geoJson);
        System.out.println("Feature: " + feature);
        if (feature == null) continue;
        // TODO: CHECK IF FEATURE ALREADY EXISTS BEFORE INSERTING
        featureDbService.insertOne(feature);
        addedFeatures++;
      }
      if (addedFeatures == 0){ // no features, remove article from DB
        articleDbService.deleteById(article.get_id());
        LOGGER.info("No features found for article "+ article.get_id() + ". Deleting from DB...");
        return;
      }
      Double sentimentScore = getSentimentScore(headline);
      System.out.println(sentimentScore);

      updateArticle(article, sentimentScore, locations); // Update article fields and update document in DB

      // MODIFY DB ARTICLE
    } catch (Exception e){
      System.err.println("Could not process article " + article.get_id() + ". " + e.getMessage());
    }
  }

  public void updateArticle(ArticleEntity article, Double sentimentScore, List<String> locations){
    ArticleEntity updatedArticle = article;
    updatedArticle.setSentimentScore(sentimentScore);
    updatedArticle.setAssociatedLocations(locations);
    articleDbService.saveArticle(updatedArticle);
    LOGGER.info("Article " + updatedArticle.get_id() + " updated successfully");
  }

  public List<String> getEntities(String articleTitle){
    Sentence headline = new Sentence(articleTitle);
    List<String> nerTags = headline.nerTags();
    List<String> words = headline.words();
    List<String> locationEntities = new ArrayList<>();
    for (int i=0; i< nerTags.size(); i++){
      if (nerTags.get(i).equals("LOCATION")
          || nerTags.get(i).equals("COUNTRY")
          || nerTags.get(i).equals("CITY")
          || nerTags.get(i).equals("STATE_OR_PROVINCE")){
        locationEntities.add(words.get(i));
      }
      else if (nerTags.get(i).equals("NATIONALITY")){

        String nationality = words.get(i);
        String location = nationalityToCountryMap.get(nationality);
        if (location != null) {
          locationEntities.add(location);
        }
      }
    }
    return locationEntities;
  }

  public Double getSentimentScore(String articleTitle) throws
      HuggingFaceApiException,
      NumberFormatException,
      ProcessingException,
      IOException{
    List<List<SentimentAnalysisResponseScore>> scoresOuterList = sentimentAnalysisService.getSentiment(articleTitle);
    if (scoresOuterList.size()>0){
      List<SentimentAnalysisResponseScore> scoresInnerList = scoresOuterList.get(0);
      Double sentimentScore = getNormalizedWeightedAvg(scoresInnerList);
      return sentimentScore;
    }
    throw new ProcessingException("Unexpected response from Hugging Face API");
  }

  private Double getNormalizedWeightedAvg(List<SentimentAnalysisResponseScore> sentimentList) throws NumberFormatException{
    Double weightedAvg = 0.0;
    for (SentimentAnalysisResponseScore sentiment : sentimentList) {
      if (sentiment.getLabel() == null) {
        System.out.println("Could not get sentiment label");
        continue;
      }
      String label = sentiment.getLabel();
      int star;
      star = Integer.parseInt(label.substring(0, 1));
      weightedAvg += star * sentiment.getScore();
    }

    Double normalized = (weightedAvg - 1) / 4.0;
    return normalized;
  }

  public FeatureEntity convertFeatureToDbEntity(GeoJson geoJson){
    List<Feature> features = geoJson.getFeatures();
    if (features.size() < 1) return null;
    Feature feature = features.get(0);
    FeatureEntity dbFeature = new FeatureEntity();
    dbFeature.setType(feature.getType());
    dbFeature.setGeometry(feature.getGeometry());
    String location = feature.getText();
    dbFeature.setLocation(location);
    dbFeature.setPropertiesLocation(location);;
    return dbFeature;
  }
}
