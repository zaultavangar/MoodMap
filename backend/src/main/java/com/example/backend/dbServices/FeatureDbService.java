package com.example.backend.dbServices;

import com.example.backend.geocodingService.GeoJsonGeometry;
import java.util.HashMap;
import java.util.List;

import java.util.Optional;
import javax.annotation.Resource;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.example.backend.entity.FeatureEntity;
import com.example.backend.repositories.FeatureRepo;

@Service
public class FeatureDbService {
    @Resource
    private FeatureRepo featureRepo;

    public void saveOne(FeatureEntity feature){
        try {
        if (feature == null){
            System.err.println("Feature is null");
            return;
        }
        String logString = feature.get_id() == null ?
            "Successfully added feature to DB: "
            : "Successfully updated DB feature: ";
        FeatureEntity savedFeature = featureRepo.save(feature);
        System.out.println(logString + savedFeature);
        } catch (IllegalArgumentException | OptimisticLockingFailureException e){
        System.err.println("Error inserting into Features collection: " + e.getMessage());
        }
    }

    public Optional<FeatureEntity> findFeatureByLocation(String location){
        return featureRepo.findByLocation(location);
    }

    public FeatureEntity createFeatureEntity(Double lng, Double lat, String location){
        GeoJsonGeometry featureGeoJsonGeometry = new GeoJsonGeometry();
        featureGeoJsonGeometry.setType("Point");

        featureGeoJsonGeometry.setCoordinates(List.of(lng, lat));

        FeatureEntity dbFeature = FeatureEntity.builder()
            .type("Feature")
            .geoJsonGeometry(featureGeoJsonGeometry)
            .properties(new HashMap<>())
            .location(location)
            .build();

        dbFeature.setPropertiesLocation(location);

        return dbFeature;
    }


}
