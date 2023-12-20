package com.example.backend.dbServices;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;
import org.bson.types.ObjectId;
import org.joda.time.format.DateTimeFormat;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.example.backend.entity.ArticleEntity;
import com.example.backend.exceptions.UsageException;
import com.example.backend.repositories.ArticleRepository;


/**
 * Service class for handling database operations related to Article entities.
 * Provides methods for saving, deleting, finding, and searching articles in the database.
 */
@Service
public class ArticleDbService{

   @Resource
   private ArticleRepository articleRepository;

  /**
   * Saves a list of articles to the database.
   *
   * @param articlesList The list of ArticleEntity objects to be saved.
   */
   public void saveManyArticles(List<ArticleEntity> articlesList){
      try {
         if (articlesList != null && !CollectionUtils.isEmpty(articlesList)){
         articleRepository.saveAll(articlesList);
         return;
         } 
         System.err.println("Article list is null or empty");
      } catch (IllegalArgumentException | OptimisticLockingFailureException e){
         System.err.println("Error inserting into Articles collection: " + e.getMessage());
      }
   }

  /**
   * Finds an article by its ID.
   *
   * @param articleId The ObjectId of the article.
   * @return An Optional containing the found ArticleEntity or empty if not found.
   */
   public Optional<ArticleEntity> findById(ObjectId articleId){
     if (articleId == null) return Optional.empty();
     return articleRepository.findById(articleId);
   }

  /**
   * Deletes an article by its ID.
   *
   * @param articleId The ObjectId of the article to be deleted.
   */
   public void deleteById(ObjectId articleId){
      System.out.println("Deleting " + articleId);
      try {
         articleRepository.deleteById(articleId);
      } catch (Exception e){
         System.out.println("Could not delete article " + articleId + ". " + e.getMessage());
      }
   }

  /**
   * Saves a single article to the database.
   *
   * @param article The ArticleEntity object to be saved.
   */
   public void saveArticle(ArticleEntity article){
      try { // maybe check if article is null
         articleRepository.save(article);
      } catch (Exception e){
         System.err.println("Error inserting into Articles collection: " + e.getMessage());
      }
   }

  /**
   * Searches for articles within a specific date range.
   *
   * @param fromDate The start date of the search range.
   * @param toDate The end date of the search range.
   * @return A list of ArticleEntity objects within the specified date range.
   */
   public List<ArticleEntity> searchByDateRange(String fromDate, String toDate){
         LocalDateTime from = convertLocalTime(fromDate);
         LocalDateTime to = convertLocalTime(toDate);
         return articleRepository.findByDateRange(from, to);
   }

  /**
   * Searches for articles based on a given input phrase.
   *
   * @param input The input phrase for the search.
   * @return A list of ArticleEntity objects matching the input phrase.
   */
   public List<ArticleEntity> searchByInput(String input) {
      return articleRepository.searchByInput(input);
   }

  /**
   * Searches for articles based on a given input phrase and date range.
   *
   * @param input The input phrase for the search.
   * @param fromDate The start date of the search range.
   * @param toDate The end date of the search range.
   * @return A list of ArticleEntity objects matching the input phrase and date range.
   */
   public List<ArticleEntity> searchByInput(String input, String fromDate, String toDate)  {
         LocalDateTime from = convertLocalTime(fromDate);
         LocalDateTime to = convertLocalTime(toDate);
         return articleRepository.searchByInputAndDateRange(input, from, to);

   }


  /**
   * Searches for articles by a specific location (e.g. France, Brazil).
   *
   * @param location The location to search articles for.
   * @return A list of ArticleEntity objects associated with the location.
   */
  public List<ArticleEntity> searchByLocation(String location){
     return articleRepository.searchByLocation(location);
  }

  /**
   * Searches for articles by a specific location and date range.
   *
   * @param location The location to search articles for.
   * @param fromDate The start date of the search range.
   * @param toDate The end date of the search range.
   * @return A list of ArticleEntity objects associated with the location and within the date range.
   */
  public List<ArticleEntity> searchByLocation(String location, String fromDate, String toDate){
     LocalDateTime from = convertLocalTime(fromDate);
     LocalDateTime to = convertLocalTime(toDate);
     return articleRepository.searchByLocationAndDateRange(location, from, to);
  }


  /**
   * Converts a date string to LocalDateTime.
   *
   * @param inputTime The date string to be converted.
   * @return LocalDateTime representation of the input date string.
   */
   private LocalDateTime convertLocalTime(String inputTime) {
      LocalDate localDate = LocalDate.parse(inputTime);
      return localDate.atStartOfDay();
   }
}
