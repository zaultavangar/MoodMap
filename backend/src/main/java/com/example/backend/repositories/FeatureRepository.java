package com.example.backend.repositories;

import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.backend.entity.FeatureEntity;
import org.springframework.data.mongodb.repository.Query;

/**
 * MongoDB repository for FeatureEntity, providing methods for querying feature data.
 */
public interface FeatureRepository extends MongoRepository<FeatureEntity, ObjectId> {

  /**
   * Retrieves all features, selectively excluding certain fields.
   *
   * @return A list of all FeatureEntity objects with specified fields excluded.
   */
  @Query(value = "{}", fields = "{'_id' :  0, 'location':  0}")
  List<FeatureEntity> getAllFeatures();

  /**
   * Finds a feature by its location.
   *
   * @param location The location string to search for.
   * @return An Optional containing the found FeatureEntity or empty if not found.
   */
  @Query("{'location':  ?0}")
  Optional<FeatureEntity> findByLocation(String location);
}
