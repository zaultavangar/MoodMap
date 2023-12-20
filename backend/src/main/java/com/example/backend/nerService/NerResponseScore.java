package com.example.backend.nerService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the response score for a Named Entity Recognition request.
 * Includes the label (entity type) and the score.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NerResponseScore {
  private String label;
  private double score;
}
