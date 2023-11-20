package com.example.backend.controller;

import com.example.backend.guardianClient.GuardianService;
import com.example.backend.response.MoodMapResponse;
import com.example.backend.response.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import the.guardian.api.http.content.ContentResponse;
import the.guardian.api.http.editions.EditionsResponse;

import javax.annotation.Resource;


@RestController
@Slf4j
public class MoodMapController {

    @Resource
    private GuardianService guardianService;

    @GetMapping("/test")
    public MoodMapResponse test(@RequestParam @NonNull String editions) {
        EditionsResponse editionsResponse = guardianService.fetchByEdition(editions);
        if (editionsResponse != null && editionsResponse.getStatus().equals("ok")) {
            MoodMapResponse response = MoodMapResponse.successResponse();
            if (editionsResponse.getTotal() != 0) {
                response.setData(editionsResponse.getResults());
            }
            return response;
        }
        MoodMapResponse response = MoodMapResponse.failResponse(1001, "empty list");
        return response;
    }

    @GetMapping("/getContent")
    public MoodMapResponse getContent(@RequestParam @DefaultValue("") @Nullable String keyWord,
                                      @RequestParam @DefaultValue("") @Nullable String tag,
                                      @RequestParam @DefaultValue("") @Nullable String fromDate) {
        ContentResponse contentResponse = guardianService.fetchByContent(keyWord, tag, fromDate);
        if (contentResponse != null && contentResponse.getStatus().equals("ok")) {
            MoodMapResponse response = MoodMapResponse.successResponse();
            if (contentResponse.getTotal() != 0) {
                response.setData(contentResponse.getResults());
            }
            return response;
        }
        MoodMapResponse response = MoodMapResponse.failResponse(
                ResponseCode.ERROR_CALLING_GUARDIAN_CONTENT_API.getCode(),
                ResponseCode.ERROR_CALLING_GUARDIAN_CONTENT_API.getErrorMessage());
        return response;
    }

    @GetMapping("/baidu")
    public void testout() {
        guardianService.testInsert("world","","2023-11-20");
    }
}
