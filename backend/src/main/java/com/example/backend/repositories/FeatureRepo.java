package com.example.backend.repositories;


import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.backend.entity.FeatureEntity;

public interface FeatureRepo extends MongoRepository<FeatureEntity, ObjectId> {}
