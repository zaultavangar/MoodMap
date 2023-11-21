package com.example.backend.dbServices;

import java.util.List;

import com.example.backend.entity.FeatureEntity;

public interface FeatureDbService {
    public void insertMany(List<FeatureEntity> featuresList);
    public void insertOne(FeatureEntity feature);
}
