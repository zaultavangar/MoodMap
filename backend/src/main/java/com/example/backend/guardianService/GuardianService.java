package com.example.backend.guardianService;

import com.example.backend.entity.ArticleEntity;
import com.example.backend.guardianService.responseRelated.AugmentedContentItem;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import the.guardian.api.client.GuardianApi;
import the.guardian.api.entity.Content;
import the.guardian.api.http.content.ContentResponse;
import the.guardian.api.http.editions.EditionsResponse;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;


@Service
public interface GuardianService {

    public AugmentedContentItem[] fetchArticlesByDateRange(String fromDate, String toDate) throws Exception;

    // @Resource
    // private GuardianApi guardianClient;

    // @Resource
    // private ArticleService articleService;

    // public EditionsResponse fetchByEdition(String editions) {
    //     EditionsResponse editionsResponse;
    //     if (StringUtils.isEmpty(editions)) {
    //         return null;
    //     }
    //     try {
    //         editionsResponse = (EditionsResponse) guardianClient.editions().setQuery(editions).fetch();
    //         return editionsResponse;
    //     }catch (Exception e) {
    //         return null;
    //     }
    // }

    // public ContentResponse fetchByContent(String keyWord, String tag, String fromDate) {
    //     Content content = guardianClient.content();
    //     if (!StringUtils.isEmpty(keyWord)) {
    //         content.setQuery(keyWord);
    //     }
    //     if (!StringUtils.isEmpty(tag)) {
    //         content.setTag(tag);
    //     }
    //     if (!StringUtils.isEmpty(fromDate)) {
    //         content.setFromDate(fromDate);
    //     }
    //     content.setShowFields("short-url").setShowTags("contributor").setShowFields("starRating,headline,thumbnail,short-url");
    //     try {
    //         ContentResponse response = (ContentResponse) content.fetch();
    //         return response;
    //     } catch (Exception e) {
    //         return null;
    //     }
    // }


    // public List<ArticleInfor> testInsert(String keyWord, String fromDate, String toDate) {
    //     ContentResponse response = fetchByContent(keyWord, "", "2023-11-20");
    //     if (response != null && response.getStatus().equals("ok")) {

    //         List<ArticleInfor> articleInfoList = List.of(response.getResults()).stream()
    //                 .map(content -> {
    //                     ArticleInfor current = new ArticleInfor();
    //                     BeanUtils.copyProperties(content,current);
    //                     return current;
    //                 })
    //                 .collect(Collectors.toList());

    //         articleService.insert(articleInfoList);
    //         return null;

    //     }
    //     return null;
    // }
}
