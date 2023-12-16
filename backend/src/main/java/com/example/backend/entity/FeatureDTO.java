package com.example.backend.entity;

import com.example.backend.geocodingService.GeoJsonGeometry;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeatureDTO {
  private String type;
  private GeoJsonGeometry geometry;
  private Map<String, Object> properties;

}


