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


// STATUS: TESTED (TODO: may need to add better exception handling though)
@Service
public class ArticleDbService{

   @Resource
   private ArticleRepository articleRepository;

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

   public Optional<ArticleEntity> findById(ObjectId articleId){
     if (articleId == null) return Optional.empty();
     return articleRepository.findById(articleId);
   }

   public void deleteById(ObjectId articleId){
      System.out.println("Deleting " + articleId);
      try {
         articleRepository.deleteById(articleId);
      } catch (Exception e){
         System.out.println("Could not delete article " + articleId + ". " + e.getMessage());
      }
   }

   public void saveArticle(ArticleEntity article){
      try { // maybe check if article is null
         System.out.println(article);
         articleRepository.save(article);
      } catch (Exception e){
         System.err.println("Error inserting into Articles collection: " + e.getMessage());
      }
   }

   //@Cacheable(cacheNames = "articles", key = "{#fromDate, #toDate}")
   public List<ArticleEntity> searchByDateRange(String fromDate, String toDate){
         LocalDateTime from = convertLocalTime(fromDate);
         LocalDateTime to = convertLocalTime(toDate);
         return articleRepository.findByDateRange(from, to);
   }

   public List<ArticleEntity> searchByInput(String input) {
      return articleRepository.searchByInput(input);
   }

   public List<ArticleEntity> searchByInput(String input, String fromDate, String toDate)  {
         LocalDateTime from = convertLocalTime(fromDate);
         LocalDateTime to = convertLocalTime(toDate);
         return articleRepository.searchByInputAndDateRange(input, from, to);

   }


  /**
   * Searches the articles collection in MongoDB by a location (e.g. France, Brazil).
   * Can limit the search within a date range if specified.
   * @param location the location to search for
   * @return a list of articles associated with the location and matching the date range (if present)
   */

  public List<ArticleEntity> searchByLocation(String location){
     return articleRepository.searchByLocation(location);
  }

  public List<ArticleEntity> searchByLocation(String location, String fromDate, String toDate){
     LocalDateTime from = convertLocalTime(fromDate);
     LocalDateTime to = convertLocalTime(toDate);
     return articleRepository.searchByLocationAndDateRange(location, from, to);
  }


  // TODO: Handle exceptions here??
   private LocalDateTime convertLocalTime(String inputTime) {
      LocalDate localDate = LocalDate.parse(inputTime);
      return localDate.atStartOfDay();
   }
}
