package com.example.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "articles")
public class ArticleEntity {

    @Id
    private ObjectId _id;

    // private String id;

    // private String type;

    private LocalDateTime webPublicationDate;

    @TextIndexed
    private String webTitle;

    private String webUrl;

    private String thumbnail;

    private String trailText;

    @TextIndexed
    private String bodyText;

    private Float sentimentScore;

    private List<String> associatedLocations;

}
