package com.example.backend.guardianService.responseRelated;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import the.guardian.api.http.content.ContentItem;

/**
 * Represents an enhanced version of a content item from The Guardian API.
 * Includes details about the article and additional fields.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AugmentedContentItem {
  private String id;
  private String type;
  private String sectionId;
  private String sectionName;
  private String webPublicationDate;
  private String webTitle;
  private String webUrl;
  private String apiUrl;
  private String pillarId;
  private String pillarName;
  private Boolean isHosted;
  private Map<String, String> fields;

}
