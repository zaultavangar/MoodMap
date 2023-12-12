package com.example.backend.guardianService.responseRelated;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import the.guardian.api.http.content.ContentItem;
import the.guardian.api.http.content.ContentResponse;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AugmentedContentResponse {
  private String status;
  private String userTier;
  private int total;
  private int startIndex;
  private int pageSize;
  private int currentPage;
  private int pages;
  private String orderBy;
  private AugmentedContentItem[] results;
}
