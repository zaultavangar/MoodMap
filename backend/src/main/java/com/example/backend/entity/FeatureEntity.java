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

// STATUS: Tested
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
    public void setPropertiesLocation(String location) {
      properties.put("location", location);
    }

    public void setDoubleProperty(String key, Double value) {
        properties.put(key, value);
    }

    public Double getDoubleProperty(String key) {
        Object value = properties.get(key);
        return value instanceof Double ? (Double) value : null;
    }

    public FeatureDTO convertToFeatureDTO(FeatureEntity featureEntity) {
        return FeatureDTO.builder()
            .type(featureEntity.getType())
            .geometry(featureEntity.getGeoJsonGeometry())
            .properties(featureEntity.getProperties())
            .build();
    }


}
