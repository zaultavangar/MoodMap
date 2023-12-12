package com.example.backend.dbServices;

import com.example.backend.entity.FeatureDTO;
import com.example.backend.geocodingService.GeoJsonGeometry;
import java.util.HashMap;
import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.example.backend.entity.FeatureEntity;
import com.example.backend.repositories.FeatureRepository;

// STATUS: TESTED (TODO: may need to add better exception handling though)
@Service
@Slf4j
public class FeatureDbService {
    @Resource
    private FeatureRepository featureRepository;

    public void saveOne(FeatureEntity feature){
        try {
        if (feature == null){
            System.err.println("Feature is null");
            return;
        }
        String logString = feature.get_id() == null ?
            "Successfully added feature to DB: "
            : "Successfully updated DB feature: ";
        FeatureEntity savedFeature = featureRepository.save(feature);
        log.info(logString + savedFeature);
        } catch (IllegalArgumentException | OptimisticLockingFailureException e){
        System.err.println("Error inserting into Features collection: " + e.getMessage());
        }
    }

    public List<FeatureDTO> getFeatures(){
        List<FeatureEntity> features = featureRepository.getAllFeatures();
        return features.stream()
            .map(f -> f.convertToFeatureDTO(f))
            .collect(Collectors.toList());
    }

    public Optional<FeatureEntity> findFeatureByLocation(String location){
        return featureRepository.findByLocation(location);
    }

    public FeatureEntity createFeatureEntity(Double lng, Double lat, String location){
        GeoJsonGeometry featureGeoJsonGeometry = GeoJsonGeometry.builder()
            .type("Point")
            .coordinates(List.of(lng, lat))
            .build();

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
