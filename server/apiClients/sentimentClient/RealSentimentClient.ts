import axios, { AxiosResponse } from "axios";
import { BertSentiment } from "../../types";
import { SentimentClient } from "./SentimentClient";
import { ApiResponse, createApiFailureResponse, createApiSuccessResponse } from "../../apiResponses";

export class RealSentimentClient implements SentimentClient{

  // returns a promise wrapped number, corresponding to the sentiment score for the input provided
  async handleSentimentRequest(text: string): Promise<ApiResponse<number>> {
    try {
      const data = {"inputs": text.slice(0, 512)};
      const axiosRes: AxiosResponse<BertSentiment[][], BertSentiment[][]> = await axios.post(
        "https://api-inference.huggingface.co/models/nlptown/bert-base-multilingual-uncased-sentiment",
        data,
        {
          headers: {
            Authorization: "Bearer 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"
          }
        }
      );
      if (axiosRes.status === 200 && axiosRes.data){
        const bertSentimentRes: BertSentiment[][] = axiosRes.data;
        if (bertSentimentRes.length > 0){
          const normalizedWeightedScore = this.getNormalizedWeightedAvg(bertSentimentRes[0]);
          return createApiSuccessResponse<number>(normalizedWeightedScore);
        }
      }
      return createApiFailureResponse('error', axiosRes.status, 
        "Failed to execute sentiment analysis for HG's API on the input provided.");
    } catch (error: any){
      return createApiFailureResponse('error', 500, 
        error instanceof Error ? error.message: 'Unknown error.');
    }
  }

  getNormalizedWeightedAvg(bertSentimentList: BertSentiment[]): number {
    let weightedAvg: number = 0
    for (const sentiment of bertSentimentList){
      const label = sentiment.label;
      const star: number = parseInt(label[0])
      weightedAvg += star*sentiment.score; // sum up star * score 
    }
    const normalized: number = (weightedAvg-1)/4.0; // normalize between 0 and 1
    return normalized;
  }
}