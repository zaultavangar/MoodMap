import { ApiResponse, NerEntity } from "../../types";

export interface NerClient{
  handleNERRequest(text: string): Promise<ApiResponse<NerEntity[]>>
}