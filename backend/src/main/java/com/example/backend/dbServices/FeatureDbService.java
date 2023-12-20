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

/**
 * Service class for handling database operations related to Feature entities.
 * Provides methods for saving, finding, and retrieving features.
 */
@Service
@Slf4j
public class FeatureDbService {
    @Resource
    private FeatureRepository featureRepository;

    /**
     * Saves a single feature entity to the database.
     *
     * @param feature The FeatureEntity object to be saved.
     */
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

    /**
     * Retrieves all features and converts them to DTOs.
     *
     * @return A list of FeatureDTO objects representing the features.
     */
    public List<FeatureDTO> getFeatures(){
        List<FeatureEntity> features = featureRepository.getAllFeatures();
        return features.stream()
            .map(f -> f.convertToFeatureDTO(f))
            .collect(Collectors.toList());
    }

    /**
     * Finds a feature by its location.
     *
     * @param location The location string to search for.
     * @return An Optional containing the found FeatureEntity or empty if not found.
     */
    public Optional<FeatureEntity> findFeatureByLocation(String location){
        return featureRepository.findByLocation(location);
    }

    /**
     * Creates a new FeatureEntity with specified longitude, latitude, and location.
     *
     * @param lng The longitude of the feature.
     * @param lat The latitude of the feature.
     * @param location The location name.
     * @return A new FeatureEntity object.
     */
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
