package com.example.backend.dbServices;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.backend.entity.ArticleEntity;
import com.example.backend.repositories.ArticleRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


// TODO: Configure ArticleDbService to handle exceptions better and add tests for that functionality
@ExtendWith(MockitoExtension.class)
class ArticleDbServiceTest {

  @Mock
  private ArticleRepository articleRepository;

  @InjectMocks
  private ArticleDbService articleDbService;

  @Test
  void testSaveManyArticles() {
    List<ArticleEntity> articlesList = List.of(new ArticleEntity());
    articleDbService.saveManyArticles(articlesList);
    verify(articleRepository).saveAll(articlesList);
  }

  @Test
  void testSaveManyArticlesWithNoArticlesPassedIn() {
    articleDbService.saveManyArticles(new ArrayList<>());
    verifyNoInteractions(articleRepository);
  }

  @Test
  void testSaveManyArticlesWithNullPassedIn() {
    articleDbService.saveManyArticles(null);
    verifyNoInteractions(articleRepository);
  }

  @Test
  void testFindById() {
    ObjectId id = new ObjectId();
    articleDbService.findById(id);
    verify(articleRepository).findById(id);
  }

  @Test
  void testFindByIdIdWithNullId() {
    assertEquals(Optional.empty(), articleDbService.findById(null));
    verifyNoInteractions(articleRepository);
  }

  @Test
  void testDeleteById() {
    ObjectId id = new ObjectId();
    articleDbService.deleteById(id);
    verify(articleRepository).deleteById(id);
  }

  @Test
  void testSaveArticle() {
    ArticleEntity article = new ArticleEntity();
    articleDbService.saveArticle(article);
    verify(articleRepository).save(article);
  }

  @Test
  void testSaveArticleCatchesException() {
    ArticleEntity article = new ArticleEntity();
    when(articleRepository.save(any(ArticleEntity.class)))
        .thenThrow(IllegalArgumentException.class);

    articleDbService.saveArticle(article);
    verify(articleRepository).save(article);
  }

  @Test
  void testSearchByDateRange() {
    String fromDate = "2023-10-20";
    String toDate = "2023-11-30";
    articleDbService.searchByDateRange(fromDate, toDate);
    verify(articleRepository).findByDateRange(
        any(LocalDateTime.class), any(LocalDateTime.class));
  }

  @Test
  void testSearchByInput() {
    String input = "gaza";
    articleDbService.searchByInput(input);
    verify(articleRepository).searchByInput(input);
  }

  @Test
  void testSearchByInputAndDateRange() {
    String fromDate = "2023-10-20";
    String toDate = "2023-11-30";
    String input = "gaza";
    articleDbService.searchByInput(input, fromDate, toDate);
    verify(articleRepository).searchByInputAndDateRange(
        eq(input), any(LocalDateTime.class), any(LocalDateTime.class));

  }

  @Test
  void testSearchByLocation() {
    articleDbService.searchByLocation("gaza");
    verify(articleRepository).searchByLocation("gaza");
  }

  @Test
  void testSearchByLocationAndDateRange() {
    String fromDate = "2023-10-20";
    String toDate = "2023-11-30";
    String location = "gaza";
    articleDbService.searchByLocation(location, fromDate, toDate);
    verify(articleRepository).searchByLocationAndDateRange(
        eq(location), any(LocalDateTime.class), any(LocalDateTime.class));
  }
}