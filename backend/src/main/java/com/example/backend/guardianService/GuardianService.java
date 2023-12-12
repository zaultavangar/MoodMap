package com.example.backend.guardianService;

import com.example.backend.exceptions.GuardianApiException;

import com.example.backend.guardianService.responseRelated.AugmentedContentResponse;
import com.example.backend.jsonUtility.JsonUtility;
import com.squareup.moshi.Types;
import java.lang.reflect.Type;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import the.guardian.api.client.GuardianApi;
import the.guardian.api.entity.Content;
import the.guardian.api.http.AbstractResponse;
import the.guardian.api.http.content.ContentResponse;

import javax.annotation.Resource;


// STATUS: Tested
@Service
@RequiredArgsConstructor
public class GuardianService {

//    @Resource
//    private GuardianApi guardianClient;

    @Value("${guardian.key}")
    private String guardianKey;

    private final RestTemplate restTemplate;

//    public GuardianService(RestTemplate restTemplate){
//      this.restTemplate = restTemplate;
//    }

    public AugmentedContentResponse fetchArticlesByDateRange(String fromDate, String toDate, int pageNum) throws Exception{
      // Error handling for dates?

      String queryUrlTemplate = UriComponentsBuilder.fromHttpUrl("https://content.guardianapis.com/search")
          .queryParam("api-key", guardianKey)
          .queryParam("section", "world")
          .queryParam("from-date", fromDate)
          .queryParam("to-date", toDate)
          .queryParam("page", pageNum)
          .queryParam("page-size", 200)
          .queryParam("show-fields", "bodyText,thumbnail")
          .encode()
          .toUriString();

      ResponseEntity<String> response = restTemplate.getForEntity(queryUrlTemplate, String.class);
      if (!response.getStatusCode().is2xxSuccessful()){
        throw new Exception("Error calling Guardian API");
      }
      JsonUtility<Map<String, AugmentedContentResponse>> jsonUtil = new JsonUtility<>();
      Type type = Types.newParameterizedType(Map.class, String.class, AugmentedContentResponse.class);
      return jsonUtil.readJson(response.getBody(), type).get("response");
    }

}
