package com.example.backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class ApiControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Test the "getFeatures" endpoint to ensure it returns a successful response with JSON data.
     */
    @Test
    public void testGetFeaturesSuccess() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/getFeatures"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.result").value("success"))
                .andExpect(jsonPath("$.data").isArray());
    }

    /**
     * Test the "search" endpoint with a valid input to ensure it returns a successful response with JSON data.
     */
    @Test
    public void testSearchSuccess() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/search")
                        .param("input", "test"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.result").value("success"))
                .andExpect(jsonPath("$.data").isArray());
    }

    /**
     * Test the "search" endpoint with a valid input but no data to ensure it returns a successful response with an empty JSON array.
     *
     * @throws Exception if there is an error during the test
     */
    @Test
    public void testSearchSuccessWithoutData() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/search")
                        .param("input", "university"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.result").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(lessThanOrEqualTo(0)))); // Checks if the data array is empty
    }

    /**
     * Test the "search" endpoint with a valid input that returns data to ensure it returns a successful response with a non-empty JSON array.
     *
     * @throws Exception if there is an error during the test
     */
    @Test
    public void testSearchSuccessWithData() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/search")
                        .param("input", "news"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.result").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(greaterThan(0)))); // Checks if the data array is empty
    }

    /**
     * Test the "search" endpoint with a missing input parameter to ensure it returns a bad request response.
     *
     * @throws Exception if there is an error during the test
     */
    @Test
    public void testSearchMissingInputParameter() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/search"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.result").value("Required parameter 'input' is missing"));
    }

    /**
     * Test the "search" endpoint with valid date parameters to ensure it returns a successful response with JSON data.
     *
     * @throws Exception if there is an error during the test
     */
    @Test
    public void testSearchWithValidDateParameters() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/search")
                        .param("input", "news")
                        .param("fromDate", "2023-12-01")
                        .param("toDate", "2023-12-30"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.result").value("success"))
                .andExpect(jsonPath("$.data").isArray());
    }

    /**
     * Test the "search" endpoint with invalid date parameters to ensure it returns a bad request response.
     *
     * @throws Exception if there is an error during the test
     */
    @Test
    public void testSearchWithInvalidDateParameters() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/search")
                        .param("input", "news")
                        .param("fromDate", "invalid-date")
                        .param("toDate", "2023-01-31"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.result").value("fromDate is invalid or not in the format yyyy-MM-dd"));
    }

    /**
     * Test the "search" endpoint with incomplete date parameters to ensure it returns a bad request response.
     *
     * @throws Exception if there is an error during the test
     */
    @Test
    public void testSearchWithIncompleteDateParameters() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/search")
                        .param("input", "news")
                        .param("fromDate", "2023-01-01"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.result").value("Both fromDate and toDate must be provided or omitted together"));
    }


    /**
     * Test the "searchByLocation" endpoint with a valid location that returns data to ensure it returns a successful response with JSON data.
     *
     * @throws Exception if there is an error during the test
     */
    @Test
    public void testSearchByLocationSuccessWithData() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/searchByLocation")
                        .param("location", "Paris"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.result").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(greaterThan(0))));
    }

    /**
     * Test the "searchByLocation" endpoint with an unknown location to ensure it returns a successful response with an empty JSON array.
     *
     * @throws Exception if there is an error during the test
     */
    @Test
    public void testSearchByLocationSuccessWithoutData() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/searchByLocation")
                        .param("location", "UnknownLocation"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.result").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

    /**
     * Test the "searchByLocation" endpoint with a missing location parameter to ensure it returns a bad request response.
     *
     * @throws Exception if there is an error during the test
     */
    @Test
    public void testSearchByLocationMissingParameter() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/searchByLocation"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.result").value("Required parameter 'location' is missing"));
    }

    /**
     * Test the "searchByLocation" endpoint with valid date parameters to ensure it returns a successful response with JSON data.
     *
     * @throws Exception if there is an error during the test
     */
    @Test
    public void testSearchByLocationWithValidDateParameters() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/searchByLocation")
                        .param("location", "Paris")
                        .param("fromDate", "2023-01-01")
                        .param("toDate", "2023-01-31"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.result").value("success"))
                .andExpect(jsonPath("$.data").isArray());
    }

    /**
     * Test the "searchByLocation" endpoint with invalid date parameters to ensure it returns a bad request response.
     *
     * @throws Exception if there is an error during the test
     */
    @Test
    public void testSearchByLocationWithInvalidDateParameters() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/searchByLocation")
                        .param("location", "Paris")
                        .param("fromDate", "invalid-date")
                        .param("toDate", "2023-01-31"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.result").value("fromDate is invalid or not in the format yyyy-MM-dd"));
    }


    /**
     * Test the "searchByLocation" endpoint with incomplete date parameters to ensure it returns a bad request response.
     *
     * @throws Exception if there is an error during the test
     */
    @Test
    public void testSearchByLocationWithIncompleteDateParameters() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/searchByLocation")
                        .param("location", "Paris")
                        .param("fromDate", "2023-01-01"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.result").value("Both fromDate and toDate must be provided or omitted together"));
    }

    /**
     * Test the "searchByDateRange" endpoint with valid date parameters to ensure it returns a successful response with JSON data.
     *
     * @throws Exception if there is an error during the test
     */
    @Test
    public void testSearchByDateRangeSuccessWithData() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/searchByDateRange")
                        .param("fromDate", "2023-12-01")
                        .param("toDate", "2023-12-31"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.result").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(greaterThan(0))));
    }

    /**
     * Test the "searchByDateRange" endpoint with date parameters that result in no data to ensure it returns a successful response with an empty JSON array.
     *
     * @throws Exception if there is an error during the test
     */
    @Test
    public void testSearchByDateRangeSuccessWithoutData() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/searchByDateRange")
                        .param("fromDate", "2020-01-01")
                        .param("toDate", "2020-01-31"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.result").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

    /**
     * Test the "searchByDateRange" endpoint with missing date parameters to ensure it returns a bad request response.
     *
     * @throws Exception if there is an error during the test
     */
    @Test
    public void testSearchByDateRangeMissingParameters() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/searchByDateRange"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.result").value("Required parameter 'fromDate' is missing"));
    }

    /**
     * Test the "searchByDateRange" endpoint with an invalid date format to ensure it returns a bad request response.
     *
     * @throws Exception if there is an error during the test
     */
    @Test
    public void testSearchByDateRangeInvalidDateFormat() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/searchByDateRange")
                        .param("fromDate", "invalid-date")
                        .param("toDate", "2023-01-31"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.result").value("fromDate is invalid or not in the format yyyy-MM-dd"));
    }

    /**
     * Test the "searchByDateRange" endpoint with an invalid date range to ensure it returns a bad request response.
     *
     * @throws Exception if there is an error during the test
     */
    @Test
    public void testSearchByDateRangeInvalidDateRange() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/searchByDateRange")
                        .param("fromDate", "2023-12-31")
                        .param("toDate", "2023-01-01"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.result").value("Dates must be in yyyy-MM-dd format and fromDate must be less than toDate"));
    }
}
