package com.example.backend.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents an article entity in the MongoDB database.
 * Includes details like publication date, title, URL, and associated locations.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "articles")
public class ArticleEntity {

    @Id
    @Schema(example = "507f1f77bcf86cd799439011")
    private ObjectId _id;

    @Schema(example = "2023-11-29 13:56:57.674")
    private LocalDateTime webPublicationDate;

    @TextIndexed
    @Schema(example = "Breaking News...")
    private String webTitle;

    @Schema(example = "https://www.theguardian.com/world/live/2023/nov/29/israel-hamas-war-live-updates-news-ceasefire-extension-reports-israel-raid-gaza-west-bank-palestine-jenin")
    private String webUrl;

    private String thumbnail;

    private String bodyText;

    @Schema(example = "0.5")
    private Double sentimentScore;

    @Schema(example = "['France', 'Germany']")
    private List<String> associatedLocations;

    /**
     * Clears the body text of the article.
     */
    public void clearBodyText(){
        this.bodyText = "";
    }

}
