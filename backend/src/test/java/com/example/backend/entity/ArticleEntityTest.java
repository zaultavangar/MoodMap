package com.example.backend.entity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ArticleEntityTest {

  @Test
  void testClearBodyText() {
    ArticleEntity article = ArticleEntity.builder()
        ._id(new ObjectId())
        .webPublicationDate(LocalDateTime.now())
        .webTitle("Brazil fights Argentina")
        .webUrl("www.article.com")
        .thumbnail("www.thumb-nail.com")
        .bodyText("Article body text")
        .sentimentScore(0.5)
        .associatedLocations(new ArrayList<>())
        .build();

    article.clearBodyText();

    assertThat(article.getBodyText()).isEqualTo("");
  }
}
