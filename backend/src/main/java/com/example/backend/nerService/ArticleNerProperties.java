package com.example.backend.nerService;

import java.util.List;
import lombok.Builder;

@Builder
public record ArticleNerProperties(
    Integer numAssociatedFeatures,
    Double sentimentScore,
    List<String> locations) {

}
