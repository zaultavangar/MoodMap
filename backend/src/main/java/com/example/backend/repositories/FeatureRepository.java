package com.example.backend.repositories;


import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.backend.entity.FeatureEntity;
import org.springframework.data.mongodb.repository.Query;

public interface FeatureRepository extends MongoRepository<FeatureEntity, ObjectId> {

  @Query("{'location':  ?0}")
  Optional<FeatureEntity> findByLocation(String location);
}
