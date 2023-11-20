import { ApiResponse, GuardianArticle } from "../../types";

export interface GuardianClient{
  handleGuardianApiRequest(fromDate: string, toDate: string): Promise<ApiResponse<GuardianArticle[]>>
}