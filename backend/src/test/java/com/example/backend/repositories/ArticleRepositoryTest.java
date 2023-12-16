package com.example.backend.repositories;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.backend.entity.ArticleEntity;
import java.time.Month;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.bson.types.ObjectId;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

@DataMongoTest
class ArticleRepositoryTest {


  @Autowired
  private ArticleRepository articleRepository;

  private final LocalDateTime TODAY_DATE = LocalDateTime.of(2023, Month.NOVEMBER, 28, 12, 30);
  private final LocalDateTime TOMORROW_DATE = LocalDateTime.of(2023, Month.NOVEMBER, 29, 12, 30);
  private final LocalDateTime YESTERDAY_DATE = LocalDateTime.of(2023, Month.NOVEMBER, 27, 12, 30);

  private final ArticleEntity ARTICLE_1 = ArticleEntity.builder()
      ._id(new ObjectId())
      .webPublicationDate(TODAY_DATE)
      .webTitle("Deal agreed to extend Gaza ceasefire for two days, say Hamas and Qatar.")
      .webUrl("https://www.theguardian.com/world/2023/nov/27/deal-agreed-to-extend")
      .sentimentScore(0.3)
      .associatedLocations(List.of("Gaza"))
      .build();

  private final ArticleEntity ARTICLE_2 = ArticleEntity.builder()
      ._id(new ObjectId())
      .webPublicationDate(TOMORROW_DATE)
      .webTitle("London surgeon says he saw ‘massacre unfold’ while working in Gaza hospital")
      .webUrl("https://www.theguardian.com/world/2023/nov/27/london-surgeon-says")
      .sentimentScore(0.2)
      .associatedLocations(List.of("London, UK", "Gaza"))
      .build();

  private final ArticleEntity ARTICLE_3 = ArticleEntity.builder()
      ._id(new ObjectId())
      .webPublicationDate(YESTERDAY_DATE)
      .webTitle("Swine flu in the UK: what we know so far")
      .webUrl("https://www.theguardian.com/world/2023/nov/27/swine-flu-in-the-uk-what-we-know-so-far")
      .sentimentScore(0.2)
      .associatedLocations(List.of("UK"))
      .build();

  private final ArticleEntity ARTICLE_4 = ArticleEntity.builder()
      ._id(new ObjectId())
      .webPublicationDate(YESTERDAY_DATE)
      .webTitle("Finland to close border with Russia for two weeks, prime minister announces – as it happened")
      .webUrl("https://www.theguardian.com/world/live/2023/nov/28/geert-wilders-netherlands-government")
      .sentimentScore(0.2)
      .associatedLocations(List.of("Finland", "Russia"))
      .build();

  @BeforeEach
  void setUp() {
    articleRepository.saveAll(List.of(ARTICLE_1, ARTICLE_2, ARTICLE_3, ARTICLE_4));
  }

  @AfterEach
  void tearDown() {
    articleRepository.deleteAll();
  }

  /**
   * Verifies that findById successfully retrieves an article when provided with a valid ID.
   * Asserts that the retrieved article matches the expected values.
   */
  @Test
  void testFindByIdRetrievesArticleForValidId() {
    Optional<ArticleEntity> articleEntity = articleRepository.findById(ARTICLE_1.get_id());

    assertThat(articleEntity.isPresent()).isTrue();
    assertDoesNotThrow(articleEntity::get);

    assertThat(articleEntity.get().get_id()).isEqualTo(ARTICLE_1.get_id());
    assertThat(articleEntity.get().getWebPublicationDate()).isEqualTo(ARTICLE_1.getWebPublicationDate());
    assertThat(articleEntity.get().getWebTitle()).isEqualTo(ARTICLE_1.getWebTitle());
    assertThat(articleEntity.get().getWebUrl()).isEqualTo(ARTICLE_1.getWebUrl());
    assertThat(articleEntity.get().getSentimentScore()).isEqualTo(ARTICLE_1.getSentimentScore());
    assertThat(articleEntity.get().getAssociatedLocations()).isEqualTo(ARTICLE_1.getAssociatedLocations());
  }

  /**
   * Tests that findById does not retrieve an article for an invalid or non-existent ID.
   * Asserts that the returned Optional is empty and accessing it throws NoSuchElementException.
   */
  @Test
  void testFindByIdDoesNotRetrieveArticleForInvalidId(){
    Optional<ArticleEntity> articleEntity = articleRepository.findById(new ObjectId());
    assertThat(articleEntity.isPresent()).isFalse();
    assertThrows(NoSuchElementException.class, articleEntity::get);
  }

  /**
   * Tests the deleteById method by providing a valid ID.
   * Ensures that the specified article is successfully deleted from the repository.
   */
  @Test
  void testShouldDeleteByIdWhenGivenValidId() {
    // delete
    articleRepository.deleteById(ARTICLE_1.get_id());
    // try to find
    Optional<ArticleEntity> articleEntity = articleRepository.findById(ARTICLE_1.get_id());
    // not found
    assertThat(articleEntity.isPresent()).isFalse();

  }


  /**
   * Tests deleteById with an invalid ID, ensuring that it does not affect other articles.
   * Verifies that the method call does not throw any exception.
   */
  @Test
  void testShouldIgnoreDeleteByIdWhenGivenInvalidId(){
    ObjectId objectId = new ObjectId();
    assertDoesNotThrow(() -> articleRepository.deleteById(objectId));

    // Check no articles were deleted
    assertThat(articleRepository.findById(ARTICLE_1.get_id()).isPresent()).isTrue();
    assertThat(articleRepository.findById(ARTICLE_2.get_id()).isPresent()).isTrue();
    assertThat(articleRepository.findById(ARTICLE_3.get_id()).isPresent()).isTrue();
    assertThat(articleRepository.findById(ARTICLE_4.get_id()).isPresent()).isTrue();
  }


  /**
   * Tests findByDateRange for successfully finding articles within a given date range.
   * Asserts that the correct number of articles are retrieved and match the expected results.
   */
  @Test
  void testFindByDateRangeSuccessfullyFindsArticlesInGivenRange() {
    Month month = Month.NOVEMBER;
    LocalDateTime fromDate = LocalDateTime.of(2023, month, 28, 6, 30);
    LocalDateTime toDate = LocalDateTime.of(2023, month, 29, 20, 30);
    List<ArticleEntity> articles = articleRepository.findByDateRange(fromDate, toDate);

    System.out.println(articles);
    assertThat(articles.size() == 2).isTrue();

    ArticleEntity expectedArticle1 = articles.get(0);
    ArticleEntity expectedArticle2 = articles.get(1);
    assertThat(expectedArticle1.get_id()).isEqualTo(ARTICLE_1.get_id());
    assertThat(expectedArticle2.get_id()).isEqualTo(ARTICLE_2.get_id());

  }

  /**
   * Tests findByDateRange for a single day, verifying correct article retrieval.
   * Ensures the method returns only articles published within the specific date range.
   */
  @Test
  void testFindByDateRangeSuccessfullyFindsArticlesForASingleDay() {
    Month month = Month.NOVEMBER;
    LocalDateTime fromDate = LocalDateTime.of(2023, month, 29, 1, 30);
    LocalDateTime toDate = LocalDateTime.of(2023, month, 29, 20, 30);
    List<ArticleEntity> articles = articleRepository.findByDateRange(fromDate, toDate);

    assertThat(articles.size() == 1).isTrue();

    ArticleEntity expectedArticle1 = articles.get(0);
    assertThat(expectedArticle1.get_id()).isEqualTo(ARTICLE_2.get_id());
  }

  /**
   * Tests findByDateRange when no articles match the given date range.
   * Verifies that the method returns an empty list in such cases.
   */
  @Test
  void testFindByDateRangeReturnsNoArticlesWhenNoMatchFind(){
    Month month = Month.NOVEMBER;
    LocalDateTime fromDate = LocalDateTime.of(2023, month, 1, 6, 30);
    LocalDateTime toDate = LocalDateTime.of(2023, month, 1, 6, 30);
    List<ArticleEntity> articles = articleRepository.findByDateRange(fromDate, toDate);

    assertThat(articles.isEmpty()).isTrue();
  }


  /**
   * Tests searchByInput for successfully finding articles matching a given input.
   * Ensures the correct articles are retrieved based on the search criteria.
   */
  @Test
  void testSearchByInputFindsArticlesWhenMatchIsFound() {
    Month month = Month.NOVEMBER;
    List<ArticleEntity> articles = articleRepository.searchByInput("Gaza");
    assertThat(articles.size() == 2).isTrue();

    ArticleEntity expectedArticle1 = articles.get(0);
    ArticleEntity expectedArticle2 = articles.get(1);
    assertThat(expectedArticle1.get_id()).isEqualTo(ARTICLE_1.get_id());
    assertThat(expectedArticle2.get_id()).isEqualTo(ARTICLE_2.get_id());

  }

  /**
   * Tests searchByInput with a search term that matches no articles.
   * Verifies that the method returns an empty list when no matches are found.
   */
  @Test
  void testSearchByInputReturnsNoArticlesWhenNoMatchIsFound() {
    List<ArticleEntity> articles = articleRepository.searchByInput("Mozambique");
    assertThat(articles.isEmpty()).isTrue();
  }

  /**
   * Tests searchByInputAndDateRange for filtering articles based on both input and date range.
   * Verifies that only articles matching both criteria are returned.
   */
  @Test
  void testSearchByInputAndDateRangeSuccessfullyFiltersOutBasedOnDate() {
    Month month = Month.NOVEMBER;
    LocalDateTime fromDate = LocalDateTime.of(2023, month, 28, 1, 30);
    LocalDateTime toDate = LocalDateTime.of(2023, month, 28, 20, 30);
    List<ArticleEntity> articles = articleRepository.searchByInputAndDateRange("Gaza", fromDate, toDate);

    // filters out ARTICLE_2 from gaza related articles based on date range
    assertThat(articles.size() == 1).isTrue();

    ArticleEntity expectedArticle = articles.get(0);
    assertThat(expectedArticle.get_id()).isEqualTo(ARTICLE_1.get_id());
  }

  /**
   * Tests the searchByInputAndDateRange method to check its efficiency in filtering articles based on input and a specific date range.
   * This test ensures that the method filters out articles accurately when both a specific input ("Russia") and a precise date range (a single day in November 2023) are provided.
   * The test asserts that the correct number of articles, which match the given input and fall within the specified date range, are retrieved.
   * It further verifies that the retrieved articles' IDs match the expected IDs, ensuring the method's effectiveness in simultaneously filtering by text input and date range.
   * This test is vital to confirm the repository's capability in handling complex queries that involve both textual and temporal criteria.
   */
  @Test
  void testSearchByInputAndDateRangeSuccessfullyFiltersOutBasedOnInput() {
    Month month = Month.NOVEMBER;
    LocalDateTime fromDate = LocalDateTime.of(2023, month, 27, 1, 30);
    LocalDateTime toDate = LocalDateTime.of(2023, month, 27, 20, 30);
    List<ArticleEntity> articles = articleRepository.searchByInputAndDateRange("Russia", fromDate, toDate);

    // filters out ARTICLE_3 from 11/27 articles based on input
    assertThat(articles.size() == 1).isTrue();

    ArticleEntity expectedArticle = articles.get(0);
    assertThat(expectedArticle.get_id()).isEqualTo(ARTICLE_4.get_id());
  }

  /**
   * Tests searchByLocation for successfully finding articles related to a specific location.
   * Checks if the retrieved articles are accurately associated with the given location.
   */
  @Test
  void testSearchByInputAndDateRangeReturnsNoArticlesWhenNoMatchIsFound() {
    Month month = Month.NOVEMBER;
    LocalDateTime fromDate = LocalDateTime.of(2022, month, 1, 1, 30);
    LocalDateTime toDate = LocalDateTime.of(2021, month, 1, 20, 30);
    List<ArticleEntity> articles = articleRepository.searchByInputAndDateRange("Gaza", fromDate, toDate);

    assertThat(articles.isEmpty()).isTrue();
  }

  /**
   * Tests searchByLocation when no articles are associated with a given location.
   * Verifies that the method returns an empty list in such cases.
   */
  @Test
  void testSearchByLocationFindsArticlesWhenMatchIsFound() {
    Month month = Month.NOVEMBER;
    List<ArticleEntity> articles = articleRepository.searchByLocation("London, UK");
    assertThat(articles.size() == 1).isTrue();

    ArticleEntity expectedArticle = articles.get(0);
    assertThat(expectedArticle.get_id()).isEqualTo(ARTICLE_2.get_id());

  }

  /**
   * Tests searchByLocationAndDateRange for correctly filtering articles based on location and date.
   * Asserts that only articles matching both location and date range criteria are returned.
   */
  @Test
  void testSearchByLocationReturnsNoArticlesWhenNoMatchIsFound() {
    List<ArticleEntity> articles = articleRepository.searchByLocation("Mozambique");
    assertThat(articles.isEmpty()).isTrue();
  }

  /**
   * Tests searchByLocationAndDateRange for cases where no articles match the criteria.
   * Ensures that an empty list is returned when no matching articles are found.
   */
  @Test
  void testSearchByLocationAndDateRangeSuccessfullyFiltersOutBasedOnDate() {
    Month month = Month.NOVEMBER;
    LocalDateTime fromDate = LocalDateTime.of(2023, month, 28, 1, 30);
    LocalDateTime toDate = LocalDateTime.of(2023, month, 28, 20, 30);
    List<ArticleEntity> articles = articleRepository.searchByLocationAndDateRange("Gaza", fromDate, toDate);

    // filters out ARTICLE_2 from gaza related articles based on date range
    assertThat(articles.size() == 1).isTrue();

    ArticleEntity expectedArticle = articles.get(0);
    assertThat(expectedArticle.get_id()).isEqualTo(ARTICLE_1.get_id());
  }

  /**
   * Tests the searchByLocationAndDateRange method to ensure it correctly filters articles based on both location and a specified date range.
   * In this test, it verifies that the method successfully filters out articles that do not match the input location ("Russia") within the given date range.
   * Asserts that only one article, which matches the criteria of being related to "Russia" and falls within the specified date range, is returned.
   * Checks that the returned article's ID matches the expected article's ID, confirming correct filtering based on location and date range.
   */
  @Test
  void testSearchByLocationAndDateRangeSuccessfullyFiltersOutBasedOnInput() {
    Month month = Month.NOVEMBER;
    LocalDateTime fromDate = LocalDateTime.of(2023, month, 27, 1, 30);
    LocalDateTime toDate = LocalDateTime.of(2023, month, 27, 20, 30);
    List<ArticleEntity> articles = articleRepository.searchByLocationAndDateRange("Russia", fromDate, toDate);

    // filters out ARTICLE_3 from 11/27 articles based on input
    assertThat(articles.size() == 1).isTrue();

    ArticleEntity expectedArticle = articles.get(0);
    assertThat(expectedArticle.get_id()).isEqualTo(ARTICLE_4.get_id());
  }

  /**
   * Tests searchByLocationAndDateRange for cases where no articles match the criteria.
   * Ensures that an empty list is returned when no matching articles are found.
   */
  @Test
  void testSearchByLocationAndDateRangeReturnsNoArticlesWhenNoMatchIsFound() {
    Month month = Month.NOVEMBER;
    LocalDateTime fromDate = LocalDateTime.of(2022, month, 1, 1, 30);
    LocalDateTime toDate = LocalDateTime.of(2021, month, 1, 20, 30);
    List<ArticleEntity> articles = articleRepository.searchByLocationAndDateRange("Gaza", fromDate, toDate);

    assertThat(articles.isEmpty()).isTrue();
  }
}