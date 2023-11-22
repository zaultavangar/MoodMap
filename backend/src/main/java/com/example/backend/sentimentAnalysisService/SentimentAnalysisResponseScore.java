package com.example.backend.sentimentAnalysisService;

import lombok.Data;

@Data
public class SentimentAnalysisResponseScore {
  private String label;
  private double score;
}
