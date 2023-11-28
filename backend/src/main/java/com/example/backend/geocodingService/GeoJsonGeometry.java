package com.example.backend.geocodingService;

import java.util.List;
import lombok.Data;

@Data
public class GeoJsonGeometry {
  private String type;
  private List<Double> coordinates;
}
