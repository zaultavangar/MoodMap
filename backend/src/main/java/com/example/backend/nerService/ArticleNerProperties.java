package com.example.backend.nerService;

import java.util.List;
import lombok.Builder;

/**
 * A record to encapsulate properties related to Named Entity Recognition (NER) for an article.
 * Includes the number of features associated, the sentiment score, and a list of locations identified.
 */
@Builder
public record ArticleNerProperties(
    Integer numAssociatedFeatures,
    Double sentimentScore,
    List<String> locations) {

}
