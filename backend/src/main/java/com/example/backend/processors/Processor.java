package com.example.backend.processors;

import com.example.backend.dbServices.FeatureDbUpdaterService;
import com.example.backend.guardianService.responseRelated.AugmentedContentItem;
import com.example.backend.guardianService.responseRelated.AugmentedContentResponse;
import com.example.backend.nerService.ArticleNerProperties;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;


import com.example.backend.dbServices.ArticleDbService;
import com.example.backend.entity.ArticleEntity;
import com.example.backend.guardianService.GuardianService;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

// STATUS: NOT TESTED
@Service
@Slf4j
public class Processor {
  private final RabbitTemplate rabbitTemplate;
  private final String exchangeName;
  private final String routingKey;
  private final GuardianService guardianService;
  private final ArticleDbService articleDbService;
  private final FeatureDbUpdaterService featureDbUpdaterService;

  public Processor(
      GuardianService guardianService,
      ArticleDbService articleDbService,
      FeatureDbUpdaterService featureDbUpdaterService,
      RabbitTemplate rabbitTemplate,
      @Qualifier("rabbitExchangeName") String exchangeName,
      @Qualifier("rabbitRoutingKey") String routingKey){
      this.guardianService = guardianService;
      this.articleDbService = articleDbService;
      this.rabbitTemplate = rabbitTemplate;
      this.exchangeName = exchangeName;
      this.featureDbUpdaterService = featureDbUpdaterService;
      this.routingKey = routingKey;

  }

  // first check the total number and then use for loop to get each page of articles
  public void processArticles(String fromDate, String toDate, boolean isPreprocessing){
    if (StringUtils.isBlank(fromDate) || StringUtils.isBlank(toDate)) {
      return;
    }
    Optional<AugmentedContentResponse> currentResponse = fetchArticlesFromGuardianAPI(fromDate, toDate, 1);
    if (currentResponse == null || currentResponse.isEmpty()) return;
    int total = currentResponse.get().getTotal();
    int MAX_BATCH_ARTICLE_SIZE = 200;
    for (int i=1; i<(total/ MAX_BATCH_ARTICLE_SIZE) +2; i++) {
      log.info("Start processing batch: {}", i);
      currentResponse = fetchArticlesFromGuardianAPI(fromDate, toDate, i);
      if (currentResponse.isEmpty()) {
        log.error("Failure processing batch: {}", i);
        return;
      }
      if (isPreprocessing){
        processCurrentBatchForPreprocessing(currentResponse.get());
      } else {
        processCurrentBatch(currentResponse.get(), i);
      }
    }
  }

  private Optional<AugmentedContentResponse> fetchArticlesFromGuardianAPI(String fromDate, String toDate, int pageNum){
    try {
      AugmentedContentResponse response = guardianService.fetchArticlesByDateRange(fromDate, toDate, pageNum);
      if (!responseIsValid(response)) {
        log.error("Invalid response, error retrieving articles from the Guardian API.");
        return Optional.empty();
      }
      return Optional.of(response);
    } catch (Exception e) {
      log.error("Error fetching articles: {}", e.getMessage());
      return Optional.empty();
    }
  }

  public boolean responseIsValid(AugmentedContentResponse contentResponse){
    return contentResponse != null && contentResponse.getStatus().equals("ok");
  }

  private void processCurrentBatch(AugmentedContentResponse currentResponse, int batchNum){
    insertArticlesAndFeaturesIntoDB(currentResponse.getResults());
    System.out.println("Finished processing batch: " + batchNum);
    log.info("Finished processing batch: {}", batchNum);
  }

  public void insertArticlesAndFeaturesIntoDB(AugmentedContentItem[] articles){
    List<ArticleEntity> articleEntityList = Stream.of(articles).map(contentItem -> {
      if (contentItem.getType().equals("liveblog")){
        return null;
      }
      return processArticle(contentItem);
    }).filter(Objects::nonNull).collect(Collectors.toList());
    articleDbService.saveManyArticles(articleEntityList);
  }

  public ArticleEntity processArticle(AugmentedContentItem contentItem) {
    ArticleEntity article = ArticleEntity.builder()
        ._id(null)
        .webTitle(contentItem.getWebTitle())
        .webPublicationDate(formatDate(contentItem.getWebPublicationDate()))
        .webUrl(contentItem.getWebUrl())
        .thumbnail(contentItem.getFields().get("thumbnail"))
        .bodyText(contentItem.getFields().get("bodyText"))
        .associatedLocations(new ArrayList<>())
        .sentimentScore(0.5)
        .build();
    try {
      ArticleNerProperties articleNerProperties = featureDbUpdaterService.updateFeaturesForArticle(article);
      if (articleNerProperties.numAssociatedFeatures() > 0){
        article.setSentimentScore(articleNerProperties.sentimentScore());
        article.setAssociatedLocations(articleNerProperties.locations());
        article.clearBodyText(); // don't want to store full bodyText into the DB
        return article;
      }
    } catch (Exception e){
      log.error("Could not process article {}. {}", article.get_id(), e.getMessage());
    }
    return null;
  }

  private void processCurrentBatchForPreprocessing(AugmentedContentResponse currentResponse) {
    for (AugmentedContentItem article: currentResponse.getResults()){
      if (article.getType().equals("liveblog")) continue;
      try {
        ObjectId articleId = insertArticleIntoDB(article);
        if (articleId != null){
          sentMessageToDbUpdater(articleId);
        }
      } catch (Exception e){
        log.error("Unable to process article {}: {}", article.getId(), e.getMessage());
      }
    }
  }

  private ObjectId insertArticleIntoDB(AugmentedContentItem contentItem){
    ArticleEntity articleEntity = ArticleEntity.builder()
        ._id(null)
        .webTitle(contentItem.getWebTitle())
        .webPublicationDate(formatDate(contentItem.getWebPublicationDate()))
        .webUrl(contentItem.getWebUrl())
        .associatedLocations(new ArrayList<>())
        .sentimentScore(0.5)
        .thumbnail(contentItem.getFields().get("thumbnail"))
        .bodyText(contentItem.getFields().get("bodyText"))
        .build();

    articleDbService.saveArticle(articleEntity); // add to DB

    return articleEntity.get_id();
  }

  private LocalDateTime formatDate(String date) throws DateTimeParseException{
    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    return LocalDateTime.parse(date, formatter);
  }

  public void sentMessageToDbUpdater(ObjectId articleId){
    String articleIdStr = articleId.toString();
    log.info("Sending message. Article ID: {}", articleIdStr);
    rabbitTemplate.convertAndSend(exchangeName, routingKey, articleIdStr);
  }




}
