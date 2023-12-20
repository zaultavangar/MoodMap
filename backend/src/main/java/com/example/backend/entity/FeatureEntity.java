package com.example.backend.entity;

import com.example.backend.geocodingService.GeoJsonGeometry;
import java.util.HashMap;
import java.util.Map;

import lombok.Builder;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Represents a feature entity in the MongoDB database.
 * Includes geographic data and various properties like location and sentiment scores.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "features")
public class FeatureEntity {
    
    @Id
    private ObjectId _id;

    private String type;

    @Indexed(unique = true)
    private String location;

    private Map<String, Object> properties = new HashMap<>();
    // 2023-11-sentiment
    // 2023-11-count

    @GeoSpatialIndexed
    private GeoJsonGeometry geoJsonGeometry;

    /**
     * Sets the location property of the feature.
     *
     * @param location The location to be set.
     */
    public void setPropertiesLocation(String location) {
      properties.put("location", location);
    }

    /**
     * Sets a double value for a specified property key.
     *
     * @param key The property key.
     * @param value The double value to be set.
     */
    public void setDoubleProperty(String key, Double value) {
        properties.put(key, value);
    }

    /**
     * Retrieves the double value of a specified property key.
     *
     * @param key The property key.
     * @return The double value of the property, or null if not found or not a double.
     */
    public Double getDoubleProperty(String key) {
        Object value = properties.get(key);
        return value instanceof Double ? (Double) value : null;
    }

    /**
     * Converts this FeatureEntity to a FeatureDTO.
     *
     * @param featureEntity The FeatureEntity to convert.
     * @return A new FeatureDTO with data from the FeatureEntity.
     */
    public FeatureDTO convertToFeatureDTO(FeatureEntity featureEntity) {
        return FeatureDTO.builder()
            .type(featureEntity.getType())
            .geometry(featureEntity.getGeoJsonGeometry())
            .properties(featureEntity.getProperties())
            .build();
    }


}
