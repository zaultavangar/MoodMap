package com.example.backend.dbServices;

import java.util.List;

import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.example.backend.entity.FeatureEntity;
import com.example.backend.repositories.FeatureRepo;

@Service
public class FeatureDbService {
    @Resource
    private FeatureRepo featureRepo;

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


    public void insertOne(FeatureEntity feature){
        try {
        if (feature == null){
            System.err.println("Feature is null");
            return;
        }
        FeatureEntity savedFeature = featureRepo.save(feature);
        System.out.println("Successfully added feature to DB: " + savedFeature);
        } catch (IllegalArgumentException e){
        System.err.println("Error inserting into Features collection: " + e.getMessage());
        } catch (OptimisticLockingFailureException e){
        System.err.println("Error inserting into Features collection: " + e.getMessage());
        } 
    }
}
