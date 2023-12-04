package com.example.backend.geocodingService;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GeoJsonGeometry {
  private String type;
  private List<Double> coordinates;
}
