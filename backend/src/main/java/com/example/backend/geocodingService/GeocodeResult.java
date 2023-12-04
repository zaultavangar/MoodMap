package com.example.backend.geocodingService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeocodeResult {
  private String formatted_address;
  private GeocodeGeometry geometry;
}
