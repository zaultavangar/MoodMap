package com.example.backend.processor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.backend.dbServices.ArticleDbService;
import com.example.backend.entity.ArticleEntity;
import com.example.backend.guardianService.GuardianService;
import com.example.backend.guardianService.responseRelated.AugmentedContentItem;

public class Processor {
  
  private final GuardianService guardianService;
  private final ArticleDbService articleDbService;

  @Autowired
  public Processor(GuardianService guardianService, ArticleDbService articleDbService) {
      this.guardianService = guardianService;
      this.articleDbService = articleDbService;
  }

  public void processArticles(String fromDate, String toDate){
    try {
      AugmentedContentItem[] articles = guardianService.fetchArticlesByDateRange(fromDate, toDate);
    } catch (Exception e){
      // throw error, can't get articles
    }
  }

  public void processArticle(AugmentedContentItem articleItem){
    String headline = articleItem.getWebTitle();
    String bodyText = articleItem.getBodyText();

    // get locations and features from headline and bodyText
    List<String> locations = new ArrayList<>();
    // List<Feature> features = ...

    // get sentiment score 
    Float sentimentScore = 0.5f;
    
    ArticleEntity article = convertToArticleEntity(articleItem, sentimentScore, locations);
    
    // add to DB
    articleDbService.insertOne(article);
  }

  public ArticleEntity convertToArticleEntity(AugmentedContentItem articleItem, Float sentimentScore, List<String> locations){
    ArticleEntity article = new ArticleEntity();
    String date = articleItem.getWebPublicationDate();
    article.setWebPublicationDate(formatDate(date));
    article.setWebTitle(articleItem.getWebTitle());
    article.setWebUrl(articleItem.getWebUrl());
    article.setThumbnail(article.getThumbnail());
    article.setTrailText(article.getTrailText());
    article.setBodyText(article.getBodyText());
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
