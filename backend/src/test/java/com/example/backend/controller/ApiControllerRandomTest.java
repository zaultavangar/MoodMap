package com.example.backend.controller;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Random;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class ApiControllerRandomTest {

  @Autowired
  private MockMvc mockMvc;

  /**
   * Generates random inputs for fromDate and toDate and ensures that
   * every request is handled as a bad request.
   */

  @Test
  public void randomTestSearchByDateRange () throws Exception{
    for (int i = 0; i < 2; i++) {
      String fromDate = generateRandomDate();
      String toDate = generateRandomDate();

      mockMvc.perform(get("http://localhost:8080/api/searchByDateRange")
              .param("fromDate", fromDate)
              .param("toDate", toDate))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/json"))
          .andExpect(jsonPath("$.status").value(400));
    }
  }

  private static String generateRandomDate() {
    Random random = new Random();
    StringBuilder date = new StringBuilder();

    for (int i = 0; i < 10; i++) {
      char randomChar = (char) (random.nextInt(26) + 'a');
      date.append(randomChar);
    }

    return date.toString();
  }
}
