package com.example.backend.guardianService;

import com.example.backend.exceptions.GuardianApiException;

import org.springframework.stereotype.Service;
import the.guardian.api.client.GuardianApi;
import the.guardian.api.entity.Content;
import the.guardian.api.http.AbstractResponse;
import the.guardian.api.http.content.ContentResponse;

import javax.annotation.Resource;


@Service
public class GuardianService {

    @Resource
    private GuardianApi guardianClient;

    public ContentResponse fetchArticlesByDateRange(String fromDate, String toDate, int pageNum) throws Exception{
      // Error handling for dates?
      Content content = guardianClient.content();
      content.setFromDate(fromDate);
      content.setToDate(toDate);
      content.setSection("world");
      content.setPage(pageNum);
      content.setPageSize(200);
      content.setShowFields("trailText,bodyText,thumbnail");

      AbstractResponse response = content.fetch();
      if (response instanceof ContentResponse){
        return (ContentResponse) response;
      } 
      throw new GuardianApiException("Unable to retrieve articles from the Guardian API.");
        
    }

}
