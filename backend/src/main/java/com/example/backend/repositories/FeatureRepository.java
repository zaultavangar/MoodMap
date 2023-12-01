package com.example.backend.repositories;


import com.example.backend.entity.FeatureProjection;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.backend.entity.FeatureEntity;
import org.springframework.data.mongodb.repository.Query;

public interface FeatureRepository extends MongoRepository<FeatureEntity, ObjectId> {

  @Query(value = "{}", fields = "{'_id' :  0, 'location':  0}")
  List<FeatureEntity> getAllFeatures();

  @Query("{'location':  ?0}")
  Optional<FeatureEntity> findByLocation(String location);
}
