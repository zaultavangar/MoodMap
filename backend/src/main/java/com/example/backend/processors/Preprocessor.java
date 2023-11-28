package com.example.backend.processors;

import com.example.backend.dbServices.ArticleDbService;
import com.example.backend.dbServices.FeatureDbUpdaterService;
import com.example.backend.entity.ArticleEntity;
import com.example.backend.nerService.ArticleNerProperties;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class Preprocessor {
  private final ArticleDbService articleDbService;

  private final FeatureDbUpdaterService featureDbUpdaterService;

  public Preprocessor(
      ArticleDbService articleDbService,
      FeatureDbUpdaterService featureDbUpdaterService
  ) {
    this.articleDbService = articleDbService;
    this.featureDbUpdaterService = featureDbUpdaterService;
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
      //
      ArticleNerProperties articleNerProperties = featureDbUpdaterService.updateFeaturesForArticle(article);
      if (articleNerProperties.numAssociatedFeatures() == 0){ // no features, remove article from DB
        articleDbService.deleteById(article.get_id());
        log.info("(Preprocessing) No features found for article {}. Deleting from DB... ", article.get_id());
        return;
      }
      // Update article fields and update document in DB
      article.setSentimentScore(articleNerProperties.sentimentScore());
      article.setAssociatedLocations(articleNerProperties.locations());
      articleDbService.saveArticle(article);

    } catch (Exception e){
      log.error("Could not process article {}. {}", article.get_id(), e.getMessage());
    }
  }
}
