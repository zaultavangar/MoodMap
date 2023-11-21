package com.example.backend.controller;

import com.example.backend.Service.ArticleService;
import com.example.backend.guardianClient.GuardianService;
import com.example.backend.response.MoodMapResponse;
import com.example.backend.response.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import the.guardian.api.http.content.ContentResponse;

import javax.annotation.Resource;


@RestController
@Slf4j
public class MoodMapController {

    @Resource
    private GuardianService guardianService;

    @Resource
    private ArticleService articleService;

    /**
     * Example of how to write MoodMap project endpoint
     * @param toDate
     * @param fromDate
     * @return
     */
    @GetMapping("/getContent")
    public MoodMapResponse getContent(@RequestParam @DefaultValue("") @Nullable String toDate,
                                      @RequestParam @DefaultValue("") @Nullable String fromDate,
                                      @RequestParam @DefaultValue("") @Nullable String page,
                                      @RequestParam @DefaultValue("") @Nullable String pageSize) {
        int pageNum = 0;
        int pageSizeNum = 0;
        if (!StringUtils.isEmpty(page)) {
            pageNum = Integer.valueOf(page);
        }
        if (!StringUtils.isEmpty(pageSize)) {
            pageSizeNum = Integer.valueOf(pageSize);
        }
        ContentResponse contentResponse = guardianService.fetchByContent(fromDate, toDate, pageNum, pageSizeNum);
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

    @GetMapping("testInsert")
    public void testInsertion(@RequestParam @DefaultValue("") @Nullable String toDate,
                              @RequestParam @DefaultValue("") @Nullable String fromDate) {
        articleService.saveArticle(fromDate,toDate, null, null);
    }
}
