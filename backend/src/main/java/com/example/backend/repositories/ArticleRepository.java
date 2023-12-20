package com.example.backend.repositories;
import java.time.LocalDateTime;
import java.util.List;

import java.util.Optional;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.example.backend.entity.ArticleEntity;

/**
 * MongoDB repository interface for ArticleEntity operations.
 */
public interface ArticleRepository extends MongoRepository<ArticleEntity, ObjectId> {

  /**
   * Deletes an article by its ID.
   *
   * @param objectId The ObjectId of the article to delete.
   */
  @Override
  void deleteById(@NonNull ObjectId objectId);

  /**
   * Finds articles within a specified date range.
   *
   * @param fromDate The start date for the range.
   * @param toDate The end date for the range.
   * @return A list of ArticleEntity objects within the date range.
   */
  @Query("{'webPublicationDate': {'$gte': ?0, '$lte': ?1} }")
  List<ArticleEntity> findByDateRange(LocalDateTime fromDate, LocalDateTime toDate);

  /**
   * Searches articles by a given input text.
   *
   * @param input The input text to search in article titles.
   * @return A list of ArticleEntity objects matching the input text.
   */
  @Query("{ 'webTitle' : { '$regex': ?0, '$options': 'i' } }")
  List<ArticleEntity> searchByInput(String input);

  /**
   * Searches articles by input text and within a specified date range.
   *
   * @param input The input text to search in article titles.
   * @param fromDate The start date for the range.
   * @param toDate The end date for the range.
   * @return A list of ArticleEntity objects matching the input text and date range.
   */
  @Query("{'webPublicationDate': {'$gte': ?1, '$lte': ?2}, 'webTitle': { '$regex': ?0, '$options': 'i' }}")
  List<ArticleEntity> searchByInputAndDateRange(String input, LocalDateTime fromDate, LocalDateTime toDate);

  /**
   * Searches articles by a given location.
   *
   * @param location The location to search articles for.
   * @return A list of ArticleEntity objects associated with the specified location.
   */
  @Query("{'associatedLocations': {'$regex': ?0, '$options': 'i'}}")
  List<ArticleEntity> searchByLocation(String location);

  /**
   * Searches articles by a location and within a specified date range.
   *
   * @param location The location to search articles for.
   * @param fromDate The start date for the range.
   * @param toDate The end date for the range.
   * @return A list of ArticleEntity objects associated with the location and within the date range.
   */
  @Query("{'webPublicationDate': { '$gte': ?1, '$lte': ?2 }, 'associatedLocations': {'$regex': ?0, '$options': 'i'} }")
  List<ArticleEntity> searchByLocationAndDateRange(String location, LocalDateTime fromDate, LocalDateTime toDate);


}

