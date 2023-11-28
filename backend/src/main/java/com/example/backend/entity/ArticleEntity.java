package com.example.backend.entity;

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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "articles")
public class ArticleEntity {

    @Id
    private ObjectId _id;

    private LocalDateTime webPublicationDate;

    @TextIndexed
    private String webTitle;

    private String webUrl;

    private Double sentimentScore;

    private List<String> associatedLocations;
}
