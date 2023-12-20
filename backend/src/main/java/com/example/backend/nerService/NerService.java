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
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Service class for performing Named Entity Recognition and sentiment analysis.
 * Utilizes the Hugging Face API for sentiment analysis.
 */
@Service
public class NerService {
  private static final String API_URL = "https://api-inference.huggingface.co/models/nlptown/bert-base-multilingual-uncased-sentiment";
  private final HuggingFaceConfig config;
  private final RestTemplate restTemplate;
  private final Map<String, String> nationalityToCountryMap;

  /**
   * Constructor for the NerService.
   * Initializes the Hugging Face configuration, RestTemplate, and loads nationality to country mappings.
   *
   * @param config The Hugging Face configuration containing the API key.
   * @param restTemplate The RestTemplate for making HTTP requests.
   * @throws IOException if there is an issue loading the nationality to country mappings.
   */
  public NerService (
      HuggingFaceConfig config,
      RestTemplate restTemplate
  ) throws IOException {
    this.config = config;
    this.restTemplate = restTemplate;
    this.nationalityToCountryMap = new HashMap<>();

     loadNationalityToCountryMap("/Users/zaultavangar/Desktop/School/Fall 2023/CSCI 1340/MoodMap/MoodMap/backend/data/countries.csv");
  }

  /**
   * Makes a sentiment analysis request to Hugging Face API.
   *
   * @param text The text to analyze.
   * @return A list of response scores from the API.
   * @throws HuggingFaceApiException if an error occurs with the API call.
   * @throws IOException if an error occurs in processing the response.
   */
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

  /**
   * Loads a nationality to country mapping from a CSV file.
   *
   * @param filePath The file path to the CSV.
   * @throws IOException if an error occurs in reading the file.
   */
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

  /**
   * Extracts named entities from an article title.
   *
   * @param articleTitle The title of the article.
   * @return A list of location entities identified in the title.
   */
  public List<String> getEntities(String articleTitle){
    List<String> locationEntities = new ArrayList<>();
    if (articleTitle == null || StringUtils.isEmpty(articleTitle.trim())) return locationEntities;
    Sentence headline = new Sentence(articleTitle);
    List<String> nerTags = headline.nerTags();
    List<String> words = headline.words();

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

  /**
   * Determines if a given NER tag corresponds to a location entity.
   *
   * @param nerTag The NER tag to evaluate.
   * @return true if the tag represents a location, country, city, or state/province, false otherwise.
   */
  private boolean isLocationEntity(String nerTag) {
    return nerTag.equals("LOCATION") || nerTag.equals("COUNTRY") ||
        nerTag.equals("CITY") || nerTag.equals("STATE_OR_PROVINCE");
  }

  /**
   * Calculates the sentiment score for an article title using Hugging Face API.
   *
   * @param articleTitle The title of the article to analyze.
   * @return The normalized weighted average sentiment score.
   * @throws HuggingFaceApiException if there is an error in the API call.
   * @throws NumberFormatException if there is an error in parsing the sentiment labels.
   * @throws ProcessingException if an unexpected response is received from the API.
   * @throws IOException if there is an error in handling the API response.
   */
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

  /**
   * Calculates the normalized weighted average of sentiment scores.
   *
   * @param sentimentList A list of sentiment response scores.
   * @return The normalized weighted average sentiment score.
   * @throws NumberFormatException if there is an error in parsing the sentiment labels.
   */
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
