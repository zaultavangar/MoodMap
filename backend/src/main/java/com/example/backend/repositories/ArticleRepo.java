package com.example.backend.repositories;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.example.backend.entity.ArticleEntity;

public interface ArticleRepo extends MongoRepository<ArticleEntity, ObjectId> {

  @Query("{'webPublicationDate': {'$gte': ?0, '$lte': ?1} }")
  List<ArticleEntity> findByDateRange(LocalDateTime fromDate, LocalDateTime toDate);

  @Query("{ 'webTitle' : { '$regex': ?0, '$options': 'i' } }")
  List<ArticleEntity> searchByInput(String input);

  @Query("{'webPublicationDate': {'$gte': ?1, '$lte': ?2}, 'webTitle': { '$regex': ?0, '$options': 'i' }}")
  List<ArticleEntity> searchByInputAndDateRange(String input, LocalDateTime fromDate, LocalDateTime toDate);

  @Query("{'associatedLocations': {'$regex': ?0, '$options': 'i'}}")
  List<ArticleEntity> searchByLocation(String location);

  @Query("{'webPublicationDate': { '$gte': ?1, '$lte': ?2 }, 'associatedLocations': {'$regex': ?0, '$options': 'i'} }")
  List<ArticleEntity> searchByLocationAndDateRange(String location, LocalDateTime fromDate, LocalDateTime toDate);
}

