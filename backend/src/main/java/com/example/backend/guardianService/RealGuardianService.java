package com.example.backend.guardianService;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.example.backend.exceptions.GuardianApiException;
import com.example.backend.guardianService.responseRelated.AugmentedContentItem;
import com.example.backend.guardianService.responseRelated.AugmentedContentResponse;

import the.guardian.api.client.GuardianApi;
import the.guardian.api.entity.Content;
import the.guardian.api.http.AbstractResponse;

@Service
public class RealGuardianService implements GuardianService{

    @Resource
    private GuardianApi guardianClient;

    @Override
    public AugmentedContentItem[] fetchArticlesByDangeRange(String fromDate, String toDate) throws Exception{
      // Error handling for dates?
      Content content = guardianClient.content();
      content.setFromDate(fromDate);
      content.setToDate(toDate);
      content.setSection("world");
      content.setPage(1);
      content.setPageSize(200);
      content.setShowFields("trailText,bodyText,thumbnail");

      AbstractResponse response = content.fetch();
      if (response instanceof AugmentedContentResponse){
        AugmentedContentResponse contentRes = (AugmentedContentResponse) response;
        AugmentedContentItem[] articles = contentRes.getAugmentedResults();
        return articles;
      } 
      throw new GuardianApiException("Unable to retrieve articles from the Guardian API.");
        
    }
}
