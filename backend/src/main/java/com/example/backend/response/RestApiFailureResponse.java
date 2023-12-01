package com.example.backend.response;

import com.example.backend.entity.ArticleEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestApiFailureResponse extends RestApiResponse<Object> {

    public RestApiFailureResponse(int code, String responseMessage) {
        super(code, responseMessage, new HashMap<>());
    }
}
