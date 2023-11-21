package com.example.backend.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor

public class RestApiResponse {
    private int code;
    private String responseMessage;
    private Object data;

    // public RestApiResponse(int code, String responseMessage, T data){
    //     this.code = code;
    //     this.responseMessage = responseMessage;
    //     this.data = data;
    // }

    // success return response
    // public static RestApiResponse successResponse(T data) {
    //     RestApiResponse response = new RestApiResponse();
    //     response.setData(data);
    //     response.setCode(200);
    //     response.setResponseMessage("success");
    //     return response;
    // }

    // // fail return response
    // public static RestApiResponse failResponse(int code, String responseMessage) {
    //     RestApiResponse response = new RestApiResponse();
    //     response.code = code;
    //     response.responseMessage = responseMessage;
    //     response.data = null;
    //     return response;
    // }
}

