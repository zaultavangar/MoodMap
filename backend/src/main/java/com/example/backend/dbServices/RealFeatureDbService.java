package com.example.backend.dbServices;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.example.backend.entity.FeatureEntity;
import com.example.backend.repositories.FeatureRepo;

@Service
public class RealFeatureDbService implements FeatureDbService{
  
  @Autowired
  private FeatureRepo featureRepo;

  @Override
  public void insertMany(List<FeatureEntity> featuresList){
    try {
      if (featuresList != null && !CollectionUtils.isEmpty(featuresList)){
        featureRepo.saveAll(featuresList);
        return;
      }
      System.err.println("Feature list is null or empty");
    } catch (IllegalArgumentException e){
      System.err.println("Error inserting into Features collection: " + e.getMessage());
    } catch (OptimisticLockingFailureException e){
      System.err.println("Error inserting into Features collection: " + e.getMessage());
    }
  }

  @Override
  public void insertOne(FeatureEntity feature){
    try {
      if (feature == null){
        System.err.println("Feature is null");
        return;
      }
      featureRepo.save(feature);
    } catch (IllegalArgumentException e){
      System.err.println("Error inserting into Features collection: " + e.getMessage());
    } catch (OptimisticLockingFailureException e){
      System.err.println("Error inserting into Features collection: " + e.getMessage());
    } 
  }
}
