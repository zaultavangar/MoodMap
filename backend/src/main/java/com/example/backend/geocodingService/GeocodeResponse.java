package com.example.backend.geocodingService;

import java.util.List;
import lombok.Data;

@Data
public class GeocodeResponse {
  private List<GeocodeResult> results;
}
