package com.example.backend.response;

import com.example.backend.entity.ArticleEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * API response class specifically for a list of ArticleEntity objects.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleEntityListApiResponse extends RestApiResponse<List<ArticleEntity>> {
  private LocalDateTime timestamp;
  private int status;
  private String result;
  private List<ArticleEntity> data;
}
