package com.example.backend.processor;

import com.example.backend.dbServices.FeatureDbService;
import com.example.backend.entity.FeatureEntity;
import com.example.backend.exceptions.HuggingFaceApiException;
import com.example.backend.exceptions.ProcessingException;
import com.example.backend.jsonUtility.JsonUtility;
import com.example.backend.mapboxGeocodingService.Feature;
import com.example.backend.mapboxGeocodingService.GeoJson;
import com.example.backend.mapboxGeocodingService.MapboxGeocodingService;
import com.example.backend.rabbitMQ.RabbitMQConfig;
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
import javax.annotation.Resource;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.backend.dbServices.ArticleDbService;
import com.example.backend.entity.ArticleEntity;
import com.example.backend.guardianService.GuardianService;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import the.guardian.api.http.content.ContentItem;
import the.guardian.api.http.content.ContentResponse;
import edu.stanford.nlp.simple.*;

@Service
public class Processor {

  private static final Logger LOGGER = LoggerFactory.getLogger(Processor.class);
  private final RabbitTemplate rabbitTemplate;
  private final String exchangeName;
  private final String routingKey;
  private final GuardianService guardianService;
  private final ArticleDbService articleDbService;


  @Autowired
  public Processor(
      GuardianService guardianService,
      ArticleDbService articleDbService,
      SentimentAnalysisService sentimentAnalysisService,
      MapboxGeocodingService mapboxGeocodingService,
      FeatureDbService featureDbService,
      RabbitTemplate rabbitTemplate,
      @Qualifier("rabbitExchangeName") String exchangeName,
      @Qualifier("rabbitRoutingKey") String routingKey){
      this.guardianService = guardianService;
      this.articleDbService = articleDbService;
      this.rabbitTemplate = rabbitTemplate;
      this.exchangeName = exchangeName;
      this.routingKey = routingKey;
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
          if (article.getType().equals("liveblog")) continue;
          ArticleEntity articleEntity = convertArticleToDbEntity(article);

          // Save to DB
          ArticleEntity savedArticleEntity = articleDbService.saveArticle(articleEntity); // add to DB
          LOGGER.info("Successfully added article " + savedArticleEntity.get_id() + " to DB.");

          ObjectId articleId = articleEntity.get_id();

          if (articleId == null) continue;
          sentMessageToDbUpdater(articleId);

        } catch (Exception e){
          System.out.println("Unable to process article: " + article.getId() + ". " + e.getMessage());
          break;
        }
      }
    } catch (Exception e){
      System.out.println(e.getMessage());
      // throw error, can't get articles
    }
  }

  public ArticleEntity convertArticleToDbEntity(ContentItem articleItem){
    ArticleEntity article = new ArticleEntity();
    String date = articleItem.getWebPublicationDate();
    article.setWebPublicationDate(formatDate(date));
    article.setWebTitle(articleItem.getWebTitle());
    article.setWebUrl(articleItem.getWebUrl());
    article.setSentimentScore(0.5); // 0.5 for now
    article.setAssociatedLocations(new ArrayList<>()); // empty array for now
    return article;
  }

  public void sentMessageToDbUpdater(ObjectId articleId){
    String articleIdStr = articleIdStr = articleId.toString();
    LOGGER.info("Sending message. Exchange: " + exchangeName + ", Routing Key: " + routingKey + ", Article ID: " + articleId.toString());
    rabbitTemplate.convertAndSend(exchangeName, routingKey, articleIdStr);
  }

  private LocalDateTime formatDate(String date) throws DateTimeParseException{
    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
    return dateTime;
  }

}
