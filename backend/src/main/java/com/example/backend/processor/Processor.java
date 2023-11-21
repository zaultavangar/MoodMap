package com.example.backend.processor;

import com.example.backend.dbServices.FeatureDbService;
import com.example.backend.entity.FeatureEntity;
import com.example.backend.exceptions.HuggingFaceApiException;
import com.example.backend.exceptions.ProcessingException;
import com.example.backend.mapboxGeocodingService.Feature;
import com.example.backend.mapboxGeocodingService.GeoJson;
import com.example.backend.mapboxGeocodingService.MapboxGeocodingService;
import com.example.backend.sentimentAnalysisService.SentimentAnalysisService;
import com.example.backend.sentimentAnalysisService.SentimentAnalysisResponseScore;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.backend.dbServices.ArticleDbService;
import com.example.backend.entity.ArticleEntity;
import com.example.backend.guardianService.GuardianService;


import org.springframework.stereotype.Service;
import the.guardian.api.http.content.ContentItem;
import the.guardian.api.http.content.ContentResponse;
import edu.stanford.nlp.simple.*;

@Service
public class Processor {
  
  private final GuardianService guardianService;
  private final ArticleDbService articleDbService;

  private final FeatureDbService featureDbService;
  private final SentimentAnalysisService sentimentAnalysisService;

  private final MapboxGeocodingService mapboxGeocodingService;
  private final Map<String, String> nationalityToCountryMap;

  @Autowired
  public Processor(
      GuardianService guardianService,
      ArticleDbService articleDbService,
      SentimentAnalysisService sentimentAnalysisService,
      MapboxGeocodingService mapboxGeocodingService,
      FeatureDbService featureDbService) throws IOException{
      this.guardianService = guardianService;
      this.articleDbService = articleDbService;
      this.nationalityToCountryMap = new HashMap<>();
      this.sentimentAnalysisService = sentimentAnalysisService;
      this.mapboxGeocodingService = mapboxGeocodingService;
      this.featureDbService = featureDbService;
      loadNationalityToCountryMap("backend/src/main/java/com/example/backend/processor/data/countries.csv");

  }

  private void loadNationalityToCountryMap(String filePath) throws IOException{
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

  public void processArticles(String fromDate, String toDate){
    try {
      ContentResponse response = guardianService.fetchArticlesByDateRange(fromDate, toDate);
      if (response == null || !response.getStatus().equals("ok")){
        System.out.println("Error retrieving articles from the Guardian API.");
        return;
      }
      ContentItem[] articles = response.getResults();
      for (ContentItem article: articles){
        try {
          if (article.getType().equals("liveblog")) break;
          processArticle(article);
        } catch (Exception e){
          System.out.println("Unable to process article: " + article.getId());
          break;
        }
      }

    } catch (Exception e){
      // throw error, can't get articles
    }
  }

  public void processArticle(ContentItem articleItem){
    try {
      String headline = articleItem.getWebTitle();
      System.out.println(headline);
      List<String> locations = getEntities(headline);
      if (locations.size() == 0) return; // don't include articles with no associated locations
      System.out.println(locations);
      Double sentimentScore = getSentimentScore(headline);
//      System.out.println(sentimentScore);
      int addedFeatures = 0;
      for (String location: locations){
        GeoJson geoJson = mapboxGeocodingService.getFeatureForLocation(location);
        System.out.println("GeoJson: " + geoJson);
        FeatureEntity feature = convertFeatureToDbEntity(geoJson);
        System.out.println("Feature: " + feature);
        if (feature == null) continue;
        // TODO: CHECK IF FEATURE ALREADY EXISTS
        featureDbService.insertOne(feature);
        addedFeatures++;
      }
      if (addedFeatures > 0){
        ArticleEntity article = convertArticleToDbEntity(articleItem, sentimentScore, locations);
        articleDbService.insertOne(article);
        return;
      }
      System.out.println("Article not inserted, no associated features found");
    } catch (Exception e){
      System.out.println("Could not process article " + articleItem.getId() + ". " + e.getMessage());
    }

    // ArticleEntity article = convertToArticleEntity(articleItem, sentimentScore, locations);

    // // add to DB
    // articleDbService.insertOne(article);
  }

  public FeatureEntity convertFeatureToDbEntity(GeoJson geoJson){
    List<Feature> features = geoJson.getFeatures();
    if (features.size() < 1) return null;
    Feature feature = features.get(0);
    FeatureEntity dbFeature = new FeatureEntity();
    dbFeature.setType(feature.getType());
    dbFeature.setGeometry(feature.getGeometry());
    dbFeature.setLocation(feature.getText());
    return dbFeature;
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
    // STANFORD NLP CODE
    //    Sentence headline = new Sentence(articleTitle);
    //    SentimentClass sentiment = headline.sentiment();
    //    Double sentimentScore = 0d;
    //    System.out.println(sentiment);
    //    System.out.println("Sentiment: "+ sentiment.ordinal());
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

  public ArticleEntity convertArticleToDbEntity(ContentItem articleItem, Double sentimentScore, List<String> locations){
    ArticleEntity article = new ArticleEntity();
    String date = articleItem.getWebPublicationDate();
    article.setWebPublicationDate(formatDate(date));
    article.setWebTitle(articleItem.getWebTitle());
    article.setWebUrl(articleItem.getWebUrl());
    article.setSentimentScore(sentimentScore);
    article.setAssociatedLocations(locations);
    return article;
  }

  private LocalDateTime formatDate(String date) throws DateTimeParseException{
    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
    return dateTime;
  }

}
