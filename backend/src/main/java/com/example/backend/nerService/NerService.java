package com.example.backend.nerService;

import com.example.backend.exceptions.HuggingFaceApiException;
import com.example.backend.exceptions.ProcessingException;
import com.example.backend.jsonUtility.JsonUtility;

import com.squareup.moshi.Types;
import edu.stanford.nlp.simple.Sentence;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.ServletContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NerService {
  private static final String API_URL = "https://api-inference.huggingface.co/models/nlptown/bert-base-multilingual-uncased-sentiment";
  private final HuggingFaceConfig config;
  private final RestTemplate restTemplate;
  private final Map<String, String> nationalityToCountryMap;

  public NerService (
      HuggingFaceConfig config,
      RestTemplate restTemplate
  ) throws IOException {
    this.config = config;
    this.restTemplate = restTemplate;
    this.nationalityToCountryMap = new HashMap<>();

    Path currentRelativePath = Paths.get("");
    String absolutePath = currentRelativePath.toAbsolutePath().toString();
     loadNationalityToCountryMap(absolutePath + "/backend/data/countries.csv");
  }

  public List<List<NerResponseScore>> makeHuggingFaceSentimentRequest(String text) throws HuggingFaceApiException, IOException {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(config.getApiKey());

    String requestJson = "{\"inputs\":\"" + text + "\"}";
    HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

    ResponseEntity<String> response = restTemplate.postForEntity(API_URL, entity, String.class);

    // Handle API errors
    if (!response.getStatusCode().is2xxSuccessful()) {
      throw new HuggingFaceApiException("Error calling Hugging Face API: " + response.getStatusCode());
    }
    JsonUtility<List<List<NerResponseScore>>> jsonUtil = new JsonUtility<>();
    Type type = Types.newParameterizedType(List.class, Types.newParameterizedType(List.class, NerResponseScore.class));
    return jsonUtil.readJson(response.getBody(),type);

  }

  private void loadNationalityToCountryMap(String filePath) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(filePath));
    String line;
    while ((line = br.readLine()) != null) {
      String[] values = line.split(",");
      if (values.length >= 4) {
        String nationality = values[3];
        String countryName = values[1];
        nationalityToCountryMap.put(nationality, countryName);
      }
    }
  }

  public List<String> getEntities(String articleTitle){
    Sentence headline = new Sentence(articleTitle);
    List<String> nerTags = headline.nerTags();
    List<String> words = headline.words();
    List<String> locationEntities = new ArrayList<>();

    for (int i=0; i< nerTags.size(); i++){
      if (isLocationEntity(nerTags.get(i))) {
        StringBuilder locationBuilder = new StringBuilder(words.get(i));
        // handles locations w/ more than 1 word, e.g. West Bank
        while (i + 1 < nerTags.size() && isLocationEntity(nerTags.get(i + 1))) {
          locationBuilder.append(" ").append(words.get(i + 1));
          i++;
        }
        locationEntities.add(locationBuilder.toString());
      }
      else if (nerTags.get(i).equals("NATIONALITY")){
        String nationality = words.get(i);
        Optional.ofNullable(nationalityToCountryMap.get(nationality))
            .ifPresent(locationEntities::add);
      }
    }
    return locationEntities;
  }

  private boolean isLocationEntity(String nerTag) {
    return nerTag.equals("LOCATION") || nerTag.equals("COUNTRY") ||
        nerTag.equals("CITY") || nerTag.equals("STATE_OR_PROVINCE");
  }

  public Double getSentimentScore(String articleTitle) throws
      HuggingFaceApiException,
      NumberFormatException,
      ProcessingException,
      IOException {
    List<List<NerResponseScore>> scoresOuterList = makeHuggingFaceSentimentRequest(articleTitle);
    if (!scoresOuterList.isEmpty()){
      List<NerResponseScore> scoresInnerList = scoresOuterList.get(0);
      return getNormalizedWeightedAvg(scoresInnerList);
    }
    throw new ProcessingException("Unexpected response from Hugging Face API");
  }

  private Double getNormalizedWeightedAvg(List<NerResponseScore> sentimentList) throws NumberFormatException{
    Double weightedAvg = sentimentList.stream()
        .filter(sentiment -> sentiment.getLabel() != null)
        .map(sentiment -> {
          int star = Integer.parseInt(sentiment.getLabel().substring(0, 1));
          return star * sentiment.getScore();
        })
        .reduce(0.0, Double::sum);
    return (weightedAvg - 1) / 4.0;
  }

}
