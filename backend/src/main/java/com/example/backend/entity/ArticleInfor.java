package com.example.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "test")
public class ArticleInfor {

    @Id
    private BigInteger _id;

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

    private String hosted;

    private String relatedCountry;

}
