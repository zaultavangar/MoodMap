import axios, { AxiosResponse } from "axios";
import {  NerEntity } from "../../types";
import { NerClient } from "./NerClient";
import { ApiResponse, createApiFailureResponse, createApiSuccessResponse } from "../../apiResponses";

export class RealNerClient implements NerClient {
  
  async handleNERRequest(text: string): Promise<ApiResponse<NerEntity[]>> {
    try {
      const data = {"inputs": text};
      const axiosRes: AxiosResponse<NerEntity[]> = await axios.post(
        "https://api-inference.huggingface.co/models/dslim/bert-base-NER",
        data,
        {
          headers: {
            Authorization: "Bearer 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"
          }
        }
      );
      if (axiosRes.status === 200 && axiosRes.data){
        const allEntities = axiosRes.data;
        const locationEntities = this.extractLocationEntities(allEntities);
        return createApiSuccessResponse<NerEntity[]>(locationEntities);
      }
      return createApiFailureResponse('error', axiosRes.status, 
        "Failed to execute NER from HG's API on the input provided.")
    } catch (error: any){
      return createApiFailureResponse('error', 500, 
        error instanceof Error ? error.message : "Unknown error.")
    }
  }

  extractLocationEntities(entities: NerEntity[]): NerEntity[]{
    const locationEntities: NerEntity[] = [];
    for (const entity of entities){
      if (entity.entity_group === 'LOC'){
        locationEntities.push(entity);
      }
    }
    return locationEntities;
  }
}