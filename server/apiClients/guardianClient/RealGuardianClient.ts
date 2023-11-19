import axios, { AxiosResponse } from "axios";
import { GuardianClient } from "./GuardianClient";
import { GuardianArticle } from "../../types";
import { ApiResponse, createApiFailureResponse, createApiSuccessResponse } from "../../apiResponses";

type GuardianResponse = {
  response: {
    status: string
    userTier: string
    total: number
    startIndex: number
    pageSize: number
    currentPage: number
    pages: number
    orderBy: string
    results: GuardianArticle[]
  }
}

export class RealGuardianClient implements GuardianClient{
  
  private static readonly section = 'world';
  private static readonly pageSize = 200;
  private static readonly API_KEY = process.env.GUARDIAN_API_KEY; // TODO: make private
  private static readonly fields = 'trailText,bodyText,thumbnail';

  async handleGuardianApiRequest(fromDate: string, toDate: string): Promise<ApiResponse<GuardianArticle[]>> {
      try {
        const axiosRes : AxiosResponse<GuardianResponse, GuardianResponse> = await axios.get(`https://content.guardianapis.com/search`, {
          params: {
            section: RealGuardianClient.section,
            page: 1,
            'page-size': RealGuardianClient.pageSize,
            'show-fields': RealGuardianClient.fields,
            'from-date': fromDate,
            'to-date': toDate,
            'api-key': RealGuardianClient.API_KEY
          }
        });
        if (axiosRes.status === 200 && axiosRes.data){
          const guardianRes = axiosRes.data;
          const innerRes = guardianRes.response;
          if (innerRes && innerRes.results){
            const guardianArticles: GuardianArticle[] = innerRes.results;
            return createApiSuccessResponse<GuardianArticle[]>(guardianArticles);
          }
        }
        return createApiFailureResponse('error', axiosRes.status, 
          'Failed to retrieve articles from the Guardian API');
      } catch (error: any){
        return createApiFailureResponse('error', 500, 
          error instanceof Error ? error.message : 'Unknown error.')
      }  
    
  }

}