package com.example.backend.processors;

import com.example.backend.dbServices.ArticleDbService;
import com.example.backend.dbServices.DbUpdaterService;
import com.example.backend.entity.ArticleEntity;
import com.example.backend.nerService.ArticleNerProperties;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * Service class for preprocessing articles.
 * It listens to a RabbitMQ queue for article IDs and initiates preprocessing tasks.
 */
@Service
@Slf4j
public class Preprocessor {
  private final ArticleDbService articleDbService;

  private final DbUpdaterService dbUpdaterService;

  /**
   * Constructor for Preprocessor.
   *
   * @param articleDbService Service for database operations related to articles.
   * @param dbUpdaterService Service for updating the database with article and feature information.
   */
  public Preprocessor(
      ArticleDbService articleDbService,
      DbUpdaterService dbUpdaterService
  ) {
    this.articleDbService = articleDbService;
    this.dbUpdaterService = dbUpdaterService;
  }

  /**
   * Receives article IDs from a RabbitMQ queue and processes each article.
   *
   * @param articleIdStr The article ID in String format.
   */
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

  /**
   * Processes a given article by updating features and sentiment score.
   * Deletes the article if no features are associated.
   *
   * @param article The ArticleEntity to process.
   */
  public void processArticle(ArticleEntity article){
    try {
      //
      ArticleNerProperties articleNerProperties = dbUpdaterService.updateFeaturesForArticle(article);
      if (articleNerProperties.numAssociatedFeatures() == 0){ // no features, remove article from DB
        articleDbService.deleteById(article.get_id());
        log.info("(Preprocessing) No features found for article {}. Deleting from DB... ", article.get_id());
        return;
      }
      // Update article fields and update document in DB
      article.setSentimentScore(articleNerProperties.sentimentScore());
      article.setAssociatedLocations(articleNerProperties.locations());
      article.clearBodyText();
      articleDbService.saveArticle(article);

    } catch (Exception e){
      log.error("Could not process article {}. {}", article.get_id(), e.getMessage());
    }
  }
}
