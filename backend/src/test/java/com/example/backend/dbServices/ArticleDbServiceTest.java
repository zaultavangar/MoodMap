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
public class ArticleDbServiceTest {

  @Mock
  private ArticleRepository articleRepository;

  @InjectMocks
  private ArticleDbService articleDbService;

  /**
   * Tests the saving of multiple articles to the database.
   * Verifies that the saveAll method of articleRepository is called
   * with the provided list of articles.
   */
  @Test
  void testSaveManyArticles() {
    List<ArticleEntity> articlesList = List.of(new ArticleEntity());
    articleDbService.saveManyArticles(articlesList);
    verify(articleRepository).saveAll(articlesList);
  }

  /**
   * Tests the behavior of saving an empty list of articles.
   * Ensures that no interactions occur with the articleRepository
   * when an empty list is passed in.
   */
  @Test
  void testSaveManyArticlesWithNoArticlesPassedIn() {
    articleDbService.saveManyArticles(new ArrayList<>());
    verifyNoInteractions(articleRepository);
  }

  /**
   * Tests the handling of a null value being passed to saveManyArticles.
   * Verifies that there are no interactions with the articleRepository.
   */
  @Test
  void testSaveManyArticlesWithNullPassedIn() {
    articleDbService.saveManyArticles(null);
    verifyNoInteractions(articleRepository);
  }

  /**
   * Tests finding an article by its ID.
   * Verifies that the findById method of the articleRepository is called
   * with the provided ID.
   */
  @Test
  void testFindById() {
    ObjectId id = new ObjectId();
    articleDbService.findById(id);
    verify(articleRepository).findById(id);
  }

  /**
   * Tests the findById method with a null ID.
   * Verifies that an empty Optional is returned and no interactions
   * occur with the articleRepository.
   */
  @Test
  void testFindByIdIdWithNullId() {
    assertEquals(Optional.empty(), articleDbService.findById(null));
    verifyNoInteractions(articleRepository);
  }

  /**
   * Tests the deletion of an article by its ID.
   * Verifies that the deleteById method of the articleRepository is called
   * with the provided ID.
   */
  @Test
  void testDeleteById() {
    ObjectId id = new ObjectId();
    articleDbService.deleteById(id);
    verify(articleRepository).deleteById(id);
  }

  /**
   * Tests saving a single article to the database.
   * Verifies that the save method of the articleRepository is called
   * with the provided article.
   */
  @Test
  void testSaveArticle() {
    ArticleEntity article = new ArticleEntity();
    articleDbService.saveArticle(article);
    verify(articleRepository).save(article);
  }

  /**
   * Tests the saveArticle method's behavior when an exception is thrown.
   * Ensures that the exception is caught and the save method is called.
   */
  @Test
  void testSaveArticleCatchesException() {
    ArticleEntity article = new ArticleEntity();
    when(articleRepository.save(any(ArticleEntity.class)))
        .thenThrow(IllegalArgumentException.class);

    articleDbService.saveArticle(article);
    verify(articleRepository).save(article);
  }

  /**
   * Tests searching for articles within a specific date range.
   * Verifies the call to findByDateRange method of the articleRepository
   * with the provided date range.
   */
  @Test
  void testSearchByDateRange() {
    String fromDate = "2023-10-20";
    String toDate = "2023-11-30";
    articleDbService.searchByDateRange(fromDate, toDate);
    verify(articleRepository).findByDateRange(
        any(LocalDateTime.class), any(LocalDateTime.class));
  }

  /**
   * Tests searching for articles based on a specific input.
   * Verifies the call to the searchByInput method of the articleRepository
   * with the provided input.
   */
  @Test
  void testSearchByInput() {
    String input = "gaza";
    articleDbService.searchByInput(input);
    verify(articleRepository).searchByInput(input);
  }

  /**
   * Tests searching for articles based on a specific input and date range.
   * Verifies the call to the searchByInputAndDateRange method of the
   * articleRepository with the provided input and date range.
   */
  @Test
  void testSearchByInputAndDateRange() {
    String fromDate = "2023-10-20";
    String toDate = "2023-11-30";
    String input = "gaza";
    articleDbService.searchByInput(input, fromDate, toDate);
    verify(articleRepository).searchByInputAndDateRange(
        eq(input), any(LocalDateTime.class), any(LocalDateTime.class));

  }

  /**
   * Tests searching for articles by location.
   * Verifies that the searchByLocation method of the articleRepository
   * is called with the provided location.
   */
  @Test
  void testSearchByLocation() {
    articleDbService.searchByLocation("gaza");
    verify(articleRepository).searchByLocation("gaza");
  }

  /**
   * Tests searching for articles by location and within a specific date range.
   * Verifies the call to searchByLocationAndDateRange method of the
   * articleRepository with the provided location and date range.
   */
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