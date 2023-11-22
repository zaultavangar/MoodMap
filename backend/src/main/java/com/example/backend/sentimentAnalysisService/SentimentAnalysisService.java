package com.example.backend.sentimentAnalysisService;

import com.example.backend.exceptions.HuggingFaceApiException;
import com.example.backend.jsonUtility.JsonUtility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SentimentAnalysisService {
  private static final String API_URL = "https://api-inference.huggingface.co/models/nlptown/bert-base-multilingual-uncased-sentiment";

  @Resource
  private HuggingFaceConfig config;

  @Resource
  private RestTemplate restTemplate;

  public List<List<SentimentAnalysisResponseScore>> getSentiment(String text) throws HuggingFaceApiException, IOException {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(config.getApiKey());

    String requestJson = "{\"inputs\":\"" + text + "\"}";
    HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

    ResponseEntity<String> response = restTemplate.postForEntity(API_URL, entity, String.class);


//    ResponseEntity<SentimentAnalysisResponse> response = restTemplate.postForEntity(API_URL, entity, SentimentAnalysisResponse.class);
//    System.out.println("After: " + response);

    // Handle API errors
    if (!response.getStatusCode().is2xxSuccessful()) {
      throw new HuggingFaceApiException("Error calling Hugging Face API: " + response.getStatusCode());
    }
    JsonUtility<List<List<SentimentAnalysisResponseScore>>> jsonUtil = new JsonUtility<>();
    Type type = Types.newParameterizedType(List.class, Types.newParameterizedType(List.class, SentimentAnalysisResponseScore.class));
    List<List<SentimentAnalysisResponseScore>> res = jsonUtil.readJson(response.getBody(),type);

    return res;

//    return response.getBody();
  }

}
