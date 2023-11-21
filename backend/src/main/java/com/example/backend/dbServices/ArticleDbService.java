package com.example.backend.dbServices;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
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



   @Cacheable(cacheNames = "articles", key = "{#fromDate, #toDate}")
   public List<ArticleEntity> searchByDateRange(String fromDate, String toDate) throws Exception{
      if (StringUtils.hasLength(fromDate) && StringUtils.hasLength(toDate)){
         formatDates(fromDate, toDate); // checks if dates can be properly formatted
         List<ArticleEntity> articles = articleRepo.findByDateRange(fromDate, toDate);
         return articles;
      }
      throw new UsageException("fromDate and toDate must be specified to create a date range");
   }


   public List<ArticleEntity> searchByInput(
      String input, 
      Optional<String> fromDate, 
      Optional<String> toDate) throws Exception {
      if (StringUtils.hasLength(input)){
         List<ArticleEntity> articles = new ArrayList<>();
         if (fromDate.isPresent() && toDate.isPresent() && StringUtils.hasLength(fromDate.get()) && StringUtils.hasLength(toDate.get())){
            formatDates(fromDate.get(), toDate.get());  // checks if dates can be properly formatted
            articles = articleRepo.searchByInputAndDateRange(input, fromDate.get(), toDate.get());
         } else {
            articles = articleRepo.searchByInput(input);
         }
         return articles;
      }
      throw new UsageException("Input must be specified");
   }

  /**
   * Searches the articles collection in MongoDB by a location (e.g. France, Brazil).
   * Can limit the search within a date range if specified.
   * @param location the location to search for
   * @return a list of articles associated with the location and matching the date range (if present)
   */

   public List<ArticleEntity> searchByLocation(
      String location, 
      Optional<String> fromDate, 
      Optional<String> toDate) throws Exception {
      if (StringUtils.hasLength(location)){
         List<ArticleEntity> articles = new ArrayList<>();
         if (fromDate.isPresent() && toDate.isPresent() && StringUtils.hasLength(fromDate.get()) && StringUtils.hasLength(toDate.get())){
         formatDates(fromDate.get(), toDate.get());  // checks if dates can be properly formatted
         articles = articleRepo.searchByLocationAndDateRange(location, fromDate.get(), toDate.get());
         } else {
         articles = articleRepo.searchByLocation(location);
         }
         return articles;
      }
      throw new UsageException("Location must be specified");
   }

   private void formatDates(String fromDate, String toDate) throws DateTimeParseException{
      DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
      LocalDateTime.parse(fromDate, formatter);
      LocalDateTime.parse(toDate, formatter);
   }

}
