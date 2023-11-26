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
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DbUpdater {

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
    log.info("Message received ->  {}", articleId);

    articleDbService.findById(articleId)
        .ifPresentOrElse(
            this::processArticle,
            () -> log.error("(Preprocessing) Article ID {} not found.", articleId)
        );
  }

  public void processArticle(ArticleEntity article){
    try {
      String headline = article.getWebTitle();
      System.out.println(headline);
      List<String> locations = getEntities(headline);
      if (locations.size() == 0) { // no locations, remove article from DB
        articleDbService.deleteById(article.get_id());
        log.info("(Preprocessing) No associated locations found for article {}. Deleting from DB...", article.get_id());
        return;
      }
      int addedFeatures = 0;
      for (String location: locations){
        GeoJson geoJson = getFeatureForLocationFromMapbox(location);
        FeatureEntity feature = convertFeatureToDbEntity(geoJson);
        if (feature == null) continue;
        Optional<FeatureEntity> existingFeature = featureDbService.findFeatureByLocation(feature.getLocation());
        if (!existingFeature.isPresent()){ // check if feature already exists in DB
          featureDbService.saveOne(feature);
        }
        addedFeatures++;
      }
      if (addedFeatures == 0){ // no features, remove article from DB
        articleDbService.deleteById(article.get_id());
        log.info("(Preprocessing) No features found for article {}. Deleting from DB... ", article.get_id());
        return;
      }
      Double sentimentScore = getSentimentScore(headline);

      updateArticle(article, sentimentScore, locations); // Update article fields and update document in DB
    } catch (Exception e){
      log.error("Could not process article {}. {}", article.get_id(), e.getMessage());
    }
  }

  public GeoJson getFeatureForLocationFromMapbox(String location) throws Exception{
    return mapboxGeocodingService.getFeatureForLocation(location);
  }

  public void updateArticle(ArticleEntity article, Double sentimentScore, List<String> locations){
    ArticleEntity updatedArticle = article;
    updatedArticle.setSentimentScore(sentimentScore);
    updatedArticle.setAssociatedLocations(locations);
    articleDbService.saveArticle(updatedArticle);
    log.info("Article {} updated successfully.", updatedArticle.get_id());
  }

  public List<String> getEntities(String articleTitle){
    Sentence headline = new Sentence(articleTitle);
    List<String> nerTags = headline.nerTags();
    List<String> words = headline.words();
    List<String> locationEntities = new ArrayList<>();

    for (int i=0; i< nerTags.size(); i++){
      if (isLocationEntity(nerTags.get(i))) {
        StringBuilder locationBuilder = new StringBuilder(words.get(i));
        // handles locations w/ more than 1 word, e.g. West Bank
        while (i + 1 < nerTags.size() && isLocationEntity(nerTags.get(i + 1))) {
          locationBuilder.append(" ").append(words.get(i + 1));
          i++;
        }
        locationEntities.add(locationBuilder.toString());
      }
      else if (nerTags.get(i).equals("NATIONALITY")){
        String nationality = words.get(i);
        Optional.ofNullable(nationalityToCountryMap.get(nationality))
            .ifPresent(locationEntities::add);
      }
    }
    return locationEntities;
  }

  private boolean isLocationEntity(String nerTag) {
    return nerTag.equals("LOCATION") || nerTag.equals("COUNTRY") ||
        nerTag.equals("CITY") || nerTag.equals("STATE_OR_PROVINCE");
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
    Double weightedAvg = sentimentList.stream()
        .filter(sentiment -> sentiment.getLabel() != null)
        .map(sentiment -> {
          int star = Integer.parseInt(sentiment.getLabel().substring(0, 1));
          return star * sentiment.getScore();
        })
        .reduce(0.0, Double::sum);
    Double normalized = (weightedAvg - 1) / 4.0;
    return normalized;
  }

  public FeatureEntity convertFeatureToDbEntity(GeoJson geoJson){
    if (geoJson == null) return null;
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
