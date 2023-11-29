package com.example.backend.geocodingService;

import lombok.Data;

@Data
public class GeocodeResult {
  private String formatted_address;
  private GeocodeGeometry geometry;
}
