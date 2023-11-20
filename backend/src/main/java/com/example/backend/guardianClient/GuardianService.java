package com.example.backend.guardianClient;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import the.guardian.api.client.GuardianApi;
import the.guardian.api.entity.Content;
import the.guardian.api.http.content.ContentResponse;
import the.guardian.api.http.editions.EditionsResponse;


@Service
public class GuardianService {

    @Resource
    private GuardianApi guardianClient;

    public EditionsResponse fetchByEdition(String editions) {
        EditionsResponse editionsResponse;
        if (StringUtils.isEmpty(editions)) {
            return null;
        }
        try {
            editionsResponse = (EditionsResponse) guardianClient.editions().setQuery(editions).fetch();
            return editionsResponse;
        }catch (Exception e) {
            return null;
        }
    }

    public ContentResponse fetchByContent(String keyWord, String tag, String fromDate) {
        Content content = guardianClient.content();
        if (!StringUtils.isEmpty(keyWord)) {
            content.setQuery(keyWord);
        }
        if (!StringUtils.isEmpty(tag)) {
            content.setTag(tag);
        }
        if (!StringUtils.isEmpty(fromDate)) {
            content.setFromDate(fromDate);
        }
        content.setShowFields("short-url").setShowTags("contributor").setShowFields("starRating,headline,thumbnail,short-url");
        try {
            ContentResponse response = (ContentResponse) content.fetch();
            return response;
        } catch (Exception e) {
            return null;
        }
    }
}
