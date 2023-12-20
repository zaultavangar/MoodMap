package com.example.backend.processors;

import com.example.backend.dbServices.DbUpdaterService;
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

/**
 * Service class for processing articles.
 * Handles fetching, processing, and storing articles from The Guardian API.
 */
@Service
@Slf4j
public class Processor {
  private final RabbitTemplate rabbitTemplate;
  private final String exchangeName;
  private final String routingKey;
  private final GuardianService guardianService;
  private final ArticleDbService articleDbService;
  private final DbUpdaterService dbUpdaterService;

  /**
   * Constructor for Processor.
   *
   * @param guardianService Service for interacting with The Guardian API.
   * @param articleDbService Service for database operations related to articles.
   * @param dbUpdaterService Service for updating the database with article and feature information.
   * @param rabbitTemplate RabbitMQ template for sending messages.
   * @param exchangeName Name of the RabbitMQ exchange.
   * @param routingKey Routing key for RabbitMQ messages.
   */
  public Processor(
      GuardianService guardianService,
      ArticleDbService articleDbService,
      DbUpdaterService dbUpdaterService,
      RabbitTemplate rabbitTemplate,
      @Qualifier("rabbitExchangeName") String exchangeName,
      @Qualifier("rabbitRoutingKey") String routingKey){
      this.guardianService = guardianService;
      this.articleDbService = articleDbService;
      this.rabbitTemplate = rabbitTemplate;
      this.exchangeName = exchangeName;
      this.dbUpdaterService = dbUpdaterService;
      this.routingKey = routingKey;

  }

  /**
   * Processes articles from The Guardian API within a given date range.
   * Depending on the preprocessing flag, it either preprocesses or directly processes the articles.
   *
   * @param fromDate The start date for fetching articles.
   * @param toDate The end date for fetching articles.
   * @param isPreprocessing Flag indicating whether to preprocess or directly process the articles.
   */
  public void processArticles(String fromDate, String toDate, boolean isPreprocessing){
    if (StringUtils.isBlank(fromDate) || StringUtils.isBlank(toDate)) {
      return;
    }
    Optional<AugmentedContentResponse> currentResponse = fetchArticlesFromGuardianAPI(fromDate, toDate, 1);
    if (currentResponse.isEmpty()) return;
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

  /**
   * Fetches articles from The Guardian API for a specific page within a date range.
   *
   * @param fromDate The start date for the articles.
   * @param toDate The end date for the articles.
   * @param pageNum The page number to fetch.
   * @return An Optional containing the AugmentedContentResponse or empty if an error occurs.
   */
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

  /**
   * Validates the response received from The Guardian API.
   *
   * @param contentResponse The content response from The Guardian API.
   * @return true if the response is valid, false otherwise.
   */
  public boolean responseIsValid(AugmentedContentResponse contentResponse){
    return contentResponse != null && contentResponse.getStatus().equals("ok");
  }

  /**
   * Processes the current batch of articles for regular processing.
   * Inserts articles and their features into the database.
   *
   * @param currentResponse The current batch of articles from The Guardian API.
   * @param batchNum The batch number for logging purposes.
   */
  private void processCurrentBatch(AugmentedContentResponse currentResponse, int batchNum){
    insertArticlesAndFeaturesIntoDB(currentResponse.getResults());
    System.out.println("Finished processing batch: " + batchNum);
    log.info("Finished processing batch: {}", batchNum);
  }

  /**
   * Inserts a list of articles and their associated features into the database.
   *
   * @param articles An array of AugmentedContentItems representing the articles.
   */
  public void insertArticlesAndFeaturesIntoDB(AugmentedContentItem[] articles){
    List<ArticleEntity> articleEntityList = Stream.of(articles).map(contentItem -> {
      if (contentItem.getType().equals("liveblog")){
        return null;
      }
      return processArticle(contentItem);
    }).filter(Objects::nonNull).collect(Collectors.toList());
    articleDbService.saveManyArticles(articleEntityList);
  }

  /**
   * Processes a single article and its features.
   *
   * @param contentItem The AugmentedContentItem representing the article.
   * @return An ArticleEntity object if processing is successful, null otherwise.
   */
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
      ArticleNerProperties articleNerProperties = dbUpdaterService.updateFeaturesForArticle(article);
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

  /**
   * Processes the current batch of articles for preprocessing.
   * Inserts articles into the database and sends messages for further processing.
   *
   * @param currentResponse The current batch of articles from The Guardian API.
   */
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
  /**
   * Inserts a single article into the database.
   *
   * @param contentItem The AugmentedContentItem representing the article.
   * @return The ObjectId of the inserted article, or null if insertion fails.
   */
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

  /**
   * Formats a date string to LocalDateTime.
   *
   * @param date The date string to format.
   * @return LocalDateTime representation of the date string.
   * @throws DateTimeParseException if the date string cannot be parsed.
   */
  private LocalDateTime formatDate(String date) throws DateTimeParseException{
    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    return LocalDateTime.parse(date, formatter);
  }

  /**
   * Sends a message to the database updater queue with the article ID.
   *
   * @param articleId The ObjectId of the article.
   */
  public void sentMessageToDbUpdater(ObjectId articleId){
    String articleIdStr = articleId.toString();
    log.info("Sending message. Article ID: {}", articleIdStr);
    rabbitTemplate.convertAndSend(exchangeName, routingKey, articleIdStr);
  }




}
