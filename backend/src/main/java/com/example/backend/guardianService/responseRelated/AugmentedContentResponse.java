package com.example.backend.guardianService.responseRelated;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import the.guardian.api.http.content.ContentItem;
import the.guardian.api.http.content.ContentResponse;

/**
 * Represents an enhanced version of the content response from The Guardian API.
 * Includes metadata about the response and the articles fetched.
 */
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
