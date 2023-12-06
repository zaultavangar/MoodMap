package com.example.backend.entity;

import com.example.backend.geocodingService.GeoJsonGeometry;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// STATUS: Not tested
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeatureDTO {
  private String type;
  private GeoJsonGeometry geometry;
  private Map<String, Object> properties;

  public FeatureEntity convertToFeatureEntity(FeatureDTO featureDTO) {
    return FeatureEntity.builder()
        .type(featureDTO.getType())
        .geoJsonGeometry(featureDTO.getGeometry())
        .properties(featureDTO.getProperties())
        .build();
  }
}


