package com.example.backend.repositories;
import java.time.LocalDateTime;
import java.util.List;

import java.util.Optional;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.example.backend.entity.ArticleEntity;

// STATUS: TESTED
public interface ArticleRepository extends MongoRepository<ArticleEntity, ObjectId> {

  @Override
  void deleteById(@NonNull ObjectId objectId);

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

