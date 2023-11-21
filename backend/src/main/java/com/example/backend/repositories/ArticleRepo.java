package com.example.backend.repositories;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.example.backend.entity.ArticleEntity;

public interface ArticleRepo extends MongoRepository<ArticleEntity, ObjectId> {

  @Query("{'webPublicationDate': {$gte: ?0, $lte: ?1} }")
  List<ArticleEntity> findByDateRange(String fromDate, String toDate);

  @Query("{ 'text' : { $search : ?0 } }")
  List<ArticleEntity> searchByInput(String input);

  @Query("{'$text': { '$search': ?0 }, 'webPublicationDate': { '$gte': ?1, '$lte': ?2 } }")
  List<ArticleEntity> searchByInputAndDateRange(String input, String fromDate, String toDate);

  @Query("{'associatedLocations': ?0}")
  List<ArticleEntity> searchByLocation(String location);

  @Query("{'associatedLocations': ?0, 'webPublicationDate': { '$gte': ?1, '$lte': ?2 } }")
  List<ArticleEntity> searchByLocationAndDateRange(String location, String fromDate, String toDate);
}

