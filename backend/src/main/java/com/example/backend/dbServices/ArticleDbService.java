package com.example.backend.dbServices;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;

import com.example.backend.entity.ArticleEntity;


public interface ArticleDbService{
   public void insertMany(List<ArticleEntity> articlesList);
   public void insertOne(ArticleEntity article);

   @Cacheable(cacheNames = "articles", key = "{#fromDate, #toDate}")
   public List<ArticleEntity> searchByDateRange(String fromDate, String toDate) throws Exception;

   public List<ArticleEntity> searchByInput(String input, Optional<String> fromDate, Optional<String> toDate) throws Exception;

   public List<ArticleEntity> searchByLocation(String location, Optional<String> fromDate, Optional<String> toDate) throws Exception;

}
