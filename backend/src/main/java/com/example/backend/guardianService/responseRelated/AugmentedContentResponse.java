package com.example.backend.guardianService.responseRelated;

import the.guardian.api.http.content.ContentResponse;

public class AugmentedContentResponse extends ContentResponse{
  private AugmentedContentItem[] augmentedResults;

  public AugmentedContentItem[] getAugmentedResults() {
      return this.augmentedResults;
  }

  public void setAugmentedResults(AugmentedContentItem[] data) {
      this.augmentedResults = data;
  }
}
