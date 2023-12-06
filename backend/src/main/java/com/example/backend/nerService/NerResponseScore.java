package com.example.backend.nerService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NerResponseScore {
  private String label;
  private double score;
}
