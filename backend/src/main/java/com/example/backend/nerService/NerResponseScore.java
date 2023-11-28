package com.example.backend.nerService;

import lombok.Data;

@Data
public class NerResponseScore {
  private String label;
  private double score;
}
