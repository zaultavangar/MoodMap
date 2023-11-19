import { ApiResponse } from "../../apiResponses";

export interface SentimentClient{
  handleSentimentRequest(text: string): Promise<ApiResponse<number>>
}