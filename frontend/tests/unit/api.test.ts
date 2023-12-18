import axios from "axios";
import { describe, expect, it, vi } from "vitest";
import mockFeaturesResponse from "../../__mocks__/features.json";
import mockLocationResponse from "../../__mocks__/location.json";
import mockKeywordResponse from "../../__mocks__/keyword.json";
import api from "~logic/api";

// Mocking axios for unit testing
vi.mock("axios");
describe("API", () => {
  describe("getFeatures", () => {
    it("should be able to successfully retrieve features", async () => {
      vi.mocked(axios.get).mockResolvedValueOnce({
        data: mockFeaturesResponse,
      });
      const result = await api.getFeatures();
      expect(result).toStrictEqual({
        data: mockFeaturesResponse.data,
      });
    });
    it("should gracefully handle an error resposne", async () => {
      vi.mocked(axios.get).mockRejectedValueOnce({
        timestamp: "2023-12-13",
        status: 500,
        result: "failure",
        data: {},
      });
      const result = await api.getFeatures();
      expect(result).toStrictEqual({
        message: "failure",
      });
    });
  });

  describe("searchByLocation", () => {
    it("should be able to successfully search by location based on a given date range", async () => {
      vi.mocked(axios.get).mockResolvedValueOnce({
        data: mockLocationResponse,
      });
      const result = await api.searchByLocation(
        "Berlin, Germany",
        "2023-12-01",
        "2023-12-31"
      );
      expect(result).toStrictEqual({
        data: mockLocationResponse.data,
      });
    });

    it("should return an error when fromDate is ommitted and toDate is provided", async () => {
      vi.mocked(axios.get).mockRejectedValueOnce({
        timestamp: "2023-12-13",
        status: 500,
        result: "Both fromDate and toDate must be provided or omitted together",
        data: {},
      });
      const result = await api.searchByLocation(
        "Berlin, Germany",
        null,
        "2023-12-31"
      );
      expect(result).toStrictEqual({
        message:
          "Both fromDate and toDate must be provided or omitted together",
      });
    });

    it("should return an error when toDate is ommitted and fromDate is provided", async () => {
      vi.mocked(axios.get).mockRejectedValueOnce({
        timestamp: "2023-12-13",
        status: 500,
        result: "Both fromDate and toDate must be provided or omitted together",
        data: {},
      });
      const result = await api.searchByLocation(
        "Berlin, Germany",
        "2023-12-01",
        null
      );
      expect(result).toStrictEqual({
        message:
          "Both fromDate and toDate must be provided or omitted together",
      });
    });
  });

  describe("search", () => {
    it("should be able to successfully search by keyword given an input", async () => {
      vi.mocked(axios.get).mockResolvedValueOnce({ data: mockKeywordResponse });
      const result = await api.search("Gaza", "2023-11-01", "2023-11-20");
      expect(result).toStrictEqual({
        data: mockKeywordResponse.data,
      });
    });
    it("should return an error when fromDate is ommitted and toDate is provided", async () => {
      vi.mocked(axios.get).mockRejectedValueOnce({
        timestamp: "2023-11-20",
        status: 500,
        result: "Both fromDate and toDate must be provided or omitted together",
        data: {},
      });
      const result = await api.search("Gaza", null, "2023-11-20");
      expect(result).toStrictEqual({
        message:
          "Both fromDate and toDate must be provided or omitted together",
      });
    });

    it("should return an error when toDate is ommitted and fromDate is provided", async () => {
      vi.mocked(axios.get).mockRejectedValueOnce({
        timestamp: "2023-11-01",
        status: 500,
        result: "Both fromDate and toDate must be provided or omitted together",
        data: {},
      });
      const result = await api.search("Gaza", "2023-11-01", null);
      expect(result).toStrictEqual({
        message:
          "Both fromDate and toDate must be provided or omitted together",
      });
    });
  });
});

// describe("handleApiResponse", () => {
//   it("should handle successful API response", async () => {
//     const mockResponse = {
//       data: [
//         {
//           /* your mock data here */
//         },
//       ],
//     };

//     vi.mocked(axios.get).mockResolvedValue(mockResponse);

//     const endpoint: Endpoint = "search";
//     const queryParams = { input: "someInput" };

//     const result = await handleApiResponse(endpoint, queryParams);

//     expect(result).toEqual({
//       articles: mockResponse,
//     });
//   });

//   it("should handle API error response", async () => {
//     const mockErrorResponse = {
//       response: {
//         data: {
//           message: "Some error message",
//         },
//       },
//     };

//     vi.mocked(axios.get).mockResolvedValue(mockErrorResponse);

//     const endpoint: Endpoint = "search";
//     const queryParams = { input: "someInput" };

//     const result = await handleApiResponse(endpoint, queryParams);

//     expect(result).toEqual({
//       message: mockErrorResponse.response.data.message,
//     });
//   });
// });
