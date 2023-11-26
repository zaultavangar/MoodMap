package com.example.backend.processor;

import com.example.backend.dbServices.FeatureDbService;
import com.example.backend.dbUpdater.DbUpdater;
import com.example.backend.entity.FeatureEntity;
import com.example.backend.mapboxGeocodingService.GeoJson;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;


import com.example.backend.dbServices.ArticleDbService;
import com.example.backend.entity.ArticleEntity;
import com.example.backend.guardianService.GuardianService;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import the.guardian.api.http.content.ContentItem;
import the.guardian.api.http.content.ContentResponse;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class Processor {
  private final RabbitTemplate rabbitTemplate;
  private final String exchangeName;
  private final String routingKey;
  private final GuardianService guardianService;
  private final ArticleDbService articleDbService;
  private final FeatureDbService featureDbService;
  private final DbUpdater dbUpdater;
  private final int MAX_BATCH_ARTICLE_SIZE = 200;

  @Autowired
  public Processor(
      GuardianService guardianService,
      ArticleDbService articleDbService,
      FeatureDbService featureDbService,
      RabbitTemplate rabbitTemplate,
      DbUpdater dbUpdater,
      @Qualifier("rabbitExchangeName") String exchangeName,
      @Qualifier("rabbitRoutingKey") String routingKey){
      this.guardianService = guardianService;
      this.articleDbService = articleDbService;
      this.featureDbService = featureDbService;
      this.rabbitTemplate = rabbitTemplate;
      this.exchangeName = exchangeName;
      this.routingKey = routingKey;
      this.dbUpdater = dbUpdater;
  }

  // first check the total number and then use for loop to get each page of articles
  public void processArticles(String fromDate, String toDate, boolean isPreprocessing){

    ContentResponse currentResponse = fetchArticles(fromDate, toDate, 1);
    if (currentResponse == null) return;

    int total = currentResponse.getTotal();
    for (int i=1; i<(total/MAX_BATCH_ARTICLE_SIZE) +2; i++) {
      log.info("Start processing batch: {}", i);
      currentResponse = fetchArticles(fromDate, toDate, i);
      if (currentResponse == null) {
        log.error("Failure processing batch: {}", i);
      }
      if (isPreprocessing){
        processCurrentBatchForPreprocessing(currentResponse);
      } else {
        processCurrentBatch(currentResponse, i);
      }
    }
  }

  private ContentResponse fetchArticles(String fromDate, String toDate, int pageNum){
    try {
      ContentResponse response = guardianService.fetchArticlesByDateRange(fromDate, toDate, pageNum);
      if (!responseIsValid(response)) {
        log.error("Invalid response, error retrieving articles from the Guardian API.");
        return null;
      }
      return response;
    } catch (Exception e) {
      log.error("Error fetching articles: {}", e.getMessage());
      return null;
    }
  }

  private void processCurrentBatchForPreprocessing(ContentResponse currentResponse) {
    for (ContentItem article: currentResponse.getResults()){
      if (article.getType().equals("liveblog")) continue;
      try {
        processArticleForPreprocessing(article);
      } catch (Exception e){
        log.error("Unable to process article {}: {}", article.getId(), e.getMessage());
      }
    }
  }

  private void processCurrentBatch(ContentResponse currentResponse, int batchNum){
    getEntitiesAndInsert(currentResponse.getResults());
    System.out.println("Finished processing batch: " + batchNum);
    log.info("Finished processing batch: {}", batchNum);
  }

  private void processArticleForPreprocessing(ContentItem article){
    ArticleEntity articleEntity = convertArticleToDbEntity(article, 0.5, new ArrayList<>());
    // Save to DB
    articleDbService.saveArticle(articleEntity); // add to DB

    ObjectId articleId = articleEntity.get_id();

    if (articleId != null) {
      sentMessageToDbUpdater(articleId);
    }
  }


  public void getEntitiesAndInsert(ContentItem[] articles){
    List<ArticleEntity> articleEntityList = List.of(articles).stream().map(contentItem -> {
      if (contentItem.getType().equals("liveblog")){
        return null;
      }
      return getOneArticleEntity(contentItem);
    }).filter(Objects::nonNull).collect(Collectors.toList());
    articleDbService.saveManyArticles(articleEntityList);
  }

  public ArticleEntity getOneArticleEntity(ContentItem contentItem){
    try {
      String headline = contentItem.getWebTitle();
      List<String> locations = dbUpdater.getEntities(headline);
      if (locations.size() == 0) return null;

      int addedFeatures = 0;
      for (String location: locations){

        System.out.println("LOCATION: " + location);
        GeoJson geoJson = dbUpdater.getFeatureForLocationFromMapbox(location);
        FeatureEntity feature = dbUpdater.convertFeatureToDbEntity(geoJson);
        System.out.println("FEATURE: " + feature);
        if (feature == null) continue;
        Optional<FeatureEntity> existingFeature = featureDbService.findFeatureByLocation(feature.getLocation());
        if (!existingFeature.isPresent()){ // check if feature already exists in DB
          featureDbService.saveOne(feature);
        }
        addedFeatures++;
      }

      Double sentimentScore = 0.0;
      sentimentScore = dbUpdater.getSentimentScore(headline);

      if (addedFeatures > 0){
        ArticleEntity article = convertArticleToDbEntity(contentItem, sentimentScore, locations);
        return article;
      }
      System.out.println("Article not inserted, no associated features found");
    } catch (Exception e){
      log.error("Could not process article {}. {}.", contentItem.getId(), e.getMessage());
    }
    return null;
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

  public void sentMessageToDbUpdater(ObjectId articleId){
    String articleIdStr = articleId.toString();
    log.info("Sending message. Article ID: {}", articleIdStr);
    rabbitTemplate.convertAndSend(exchangeName, routingKey, articleIdStr);
  }

  public boolean responseIsValid(ContentResponse contentResponse){
    if (contentResponse == null || !contentResponse.getStatus().equals("ok")){
      return false;
    }
    return true;
  }

  private LocalDateTime formatDate(String date) throws DateTimeParseException{
    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
    return dateTime;
  }

}
