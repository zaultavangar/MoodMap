package com.example.backend.guardianClient;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import the.guardian.api.client.GuardianApi;
import the.guardian.api.entity.Content;
import the.guardian.api.http.content.ContentResponse;

import javax.annotation.Resource;


@Service
public class GuardianService implements GuardianInterface{

    @Resource
    private GuardianApi guardianClient;

    public ContentResponse fetchByContent(String fromDate, String toDate, int page, int pageSize) {
        Content content = guardianClient.content();
        if (!StringUtils.isEmpty(fromDate)) {
            content.setFromDate(fromDate);
        }
        if (!StringUtils.isEmpty(toDate)) {
            content.setToDate(toDate);
        }
        if (page >= 1) {
            content.setPage(page);
        }
        if (pageSize >= 1) {
            content.setPageSize(pageSize);
        }
        content.setQuery("world").setShowFields("short-url")
                .setShowTags("contributor")
                .setShowFields("starRating,headline,thumbnail,short-url");
        try {
            ContentResponse response = (ContentResponse) content.fetch();
            return response;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
}
