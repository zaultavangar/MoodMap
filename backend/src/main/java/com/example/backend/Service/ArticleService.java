package com.example.backend.Service;


public interface ArticleService {

    // insert article to mongo db
    public void saveArticle(String fromDate, String toDate, Integer page, Integer pageSize);
}
