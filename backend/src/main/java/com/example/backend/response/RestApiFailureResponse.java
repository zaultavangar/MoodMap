package com.example.backend.response;

import com.example.backend.entity.ArticleEntity;
import java.util.List;

public class RestApiFailureResponse extends RestApiResponse<List<ArticleEntity>> {

    public RestApiFailureResponse(int code, String responseMessage) {
        super(code, responseMessage, null);
    }
}
