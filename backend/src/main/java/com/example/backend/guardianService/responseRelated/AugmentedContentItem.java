package com.example.backend.guardianService.responseRelated;

import java.util.Map;

import the.guardian.api.http.content.ContentItem;

public class AugmentedContentItem extends ContentItem{
  private Map<String, String> fields;

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    public String getThumbnail() {
        return fields != null ? fields.get("thumbnail") : null;
    }

    public String getTrailText() {
        return fields != null ? fields.get("trailText") : null;
    }

    public String getBodyText() {
        return fields != null ? fields.get("bodyText") : null;
    }
}
