package com.example.backend.dbServices;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.example.backend.entity.ArticleEntity;
import com.example.backend.exceptions.UsageException;
import com.example.backend.repositories.ArticleRepo;


@Service
public class ArticleDbService{

   @Resource
   private ArticleRepo articleRepo;

   public void insertMany(List<ArticleEntity> articlesList){
      try {
         if (articlesList != null && !CollectionUtils.isEmpty(articlesList)){
         articleRepo.saveAll(articlesList);
         return;
         } 
         System.err.println("Article list is null or empty");
      } catch (IllegalArgumentException e){
         System.err.println("Error inserting into Articles collection: " + e.getMessage());
      } catch (OptimisticLockingFailureException e){
         System.err.println("Error inserting into Articles collection: " + e.getMessage());
      }
   }


   public void insertOne(ArticleEntity article){
      try {
         if (article == null){
         System.err.println("Article is null");
         return;
         }
         System.out.println(article);
         Object saved = articleRepo.save(article);
         System.out.println("Successfully added article to database: " + saved);
      } catch (IllegalArgumentException e){
         System.err.println("Error inserting into Articles collection: " + e.getMessage());
      } catch (OptimisticLockingFailureException e){
         System.err.println("Error inserting into Articles collection: " + e.getMessage());
      } 
   }

   //@Cacheable(cacheNames = "articles", key = "{#fromDate, #toDate}")
   public List<ArticleEntity> searchByDateRange(String fromDate, String toDate) throws Exception{
      if (StringUtils.hasLength(fromDate) && StringUtils.hasLength(toDate)){
         LocalDateTime from = convertLocalTime(fromDate);
         LocalDateTime to = convertLocalTime(toDate);
         List<ArticleEntity> articles = articleRepo.findByDateRange(from, to);
         return articles;
      }
      throw new UsageException("fromDate and toDate must be specified to create a date range");
   }

   public List<ArticleEntity> searchByInput(String input) throws UsageException{
      if (StringUtils.hasLength(input)){
         List<ArticleEntity> articles = articleRepo.searchByInput(input);
         return articles;
      }
      throw new UsageException("Input must be specified");
   }

   public List<ArticleEntity> searchByInput(String input, String fromDate, String toDate) throws UsageException{
      if (StringUtils.hasLength(input) && StringUtils.hasLength(fromDate) && StringUtils.hasLength(toDate)){
         LocalDateTime from = convertLocalTime(fromDate);
         LocalDateTime to = convertLocalTime(toDate);
         List<ArticleEntity> articles = articleRepo.searchByInputAndDateRange(input, from, to);
         return articles;
      }
      throw new UsageException("Input, fromDate, and toDate must be specified");
   }


  /**
   * Searches the articles collection in MongoDB by a location (e.g. France, Brazil).
   * Can limit the search within a date range if specified.
   * @param location the location to search for
   * @return a list of articles associated with the location and matching the date range (if present)
   */

  public List<ArticleEntity> searchByLocation(String location) throws UsageException{
     if (StringUtils.hasLength(location)){
        List<ArticleEntity> articles = articleRepo.searchByLocation(location);
        return articles;
     }
     throw new UsageException("Location must be specified");
  }

  public List<ArticleEntity> searchByLocation(String location, String fromDate, String toDate) throws UsageException{
     if (StringUtils.hasLength(location) && StringUtils.hasLength(fromDate) && StringUtils.hasLength(toDate)){
        LocalDateTime from = convertLocalTime(fromDate);
        LocalDateTime to = convertLocalTime(toDate);
        List<ArticleEntity> articles = articleRepo.searchByLocationAndDateRange(location, from, to);
        return articles;
     }
     throw new UsageException("Location must be specified");
  }


   private void formatDates(String fromDate, String toDate) throws DateTimeParseException{
      DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
      LocalDateTime.parse(fromDate, formatter);
      LocalDateTime.parse(toDate, formatter);
   }

   private LocalDateTime convertLocalTime(String inputTime) {
      LocalDate localDate = LocalDate.parse(inputTime);
      LocalDateTime localDateTime = localDate.atStartOfDay();
      return localDateTime;
   }
}
