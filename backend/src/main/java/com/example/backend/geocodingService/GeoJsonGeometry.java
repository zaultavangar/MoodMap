package com.example.backend.geocodingService;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GeoJsonGeometry {
  private String type;
  private List<Double> coordinates;
}
