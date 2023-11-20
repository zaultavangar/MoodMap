package com.example.backend.Repository;

import com.example.backend.entity.ArticleInfor;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ArticleRepo extends MongoRepository<ArticleInfor, String> {
}
