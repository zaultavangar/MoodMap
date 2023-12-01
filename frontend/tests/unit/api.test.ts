import axios from "axios";
import { describe, expect, it, vi } from "vitest";
import { Endpoint, handleApiResponse } from "../../src/logic/api";

// Mocking axios for unit testing
vi.mock("axios");

describe("handleApiResponse", () => {
  it("should handle successful API response", async () => {
    const mockResponse = {
      data: [
        {
          /* your mock data here */
        },
      ],
    };

    vi.mocked(axios.get).mockResolvedValue(mockResponse);

    const endpoint: Endpoint = "search";
    const queryParams = { input: "someInput" };

    const result = await handleApiResponse(endpoint, queryParams);

    expect(result).toEqual({
      articles: mockResponse,
    });
  });

  it("should handle API error response", async () => {
    const mockErrorResponse = {
      response: {
        data: {
          message: "Some error message",
        },
      },
    };

    vi.mocked(axios.get).mockResolvedValue(mockErrorResponse);

    const endpoint: Endpoint = "search";
    const queryParams = { input: "someInput" };

    const result = await handleApiResponse(endpoint, queryParams);

    expect(result).toEqual({
      message: mockErrorResponse.response.data.message,
    });
  });
});
