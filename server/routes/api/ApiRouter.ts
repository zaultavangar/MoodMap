import express, { Request, Response, Router } from "express";
import { ArticleDateRangeHandler } from "../../ArticleDateRangeHandler";
import { CachingArticlesProxy } from "../../caches/CachingArticlesProxy";
import { ApiResponse, createApiFailureResponse, createApiSuccessResponse } from "../../apiResponses";
import { DbArticle } from "../../models/Article";
import { DbGateway } from "../../DbGateway";


export class ApiRouter {
  private static dbGateway = new DbGateway;
  private static articleDateRangeHandler: ArticleDateRangeHandler = 
    new CachingArticlesProxy(this.dbGateway);
    private router: Router;

  constructor(){
    this.router = express.Router();
    this.initializeRoutes();
  }

  private initializeRoutes = () => {
    this.router.get('/searchByLngLat/:lng/:lat', this.handleSearchByLngLat);
    this.router.get('/search/:input', this.handleSearch);
    this.router.get('/searchByDateRange/:fromDate/:toDate', this.handleDateRangeRequest);
  }

  private async handleSearchByLngLat(req: Request, res: Response): Promise<ApiResponse<DbArticle[]>>{
    const lng = req.params.lng;
    const lat = req.params.lat;

    if (lng === null || lat === null){
      return {result: 'error_bad_request', statusCode: 400, error_message: 'Must provide lng and lat values.'};
    }
    const latNum = parseFloat(lat);
    const lngNum = parseFloat(lng);

    if (isNaN(latNum) || isNaN(lngNum)){
      return {result: 'error_bad_request', statusCode: 400, error_message: 'Longitude and latitude values should be numbers.'};
    }

    try {
      // TODO: can also specify from or to date in params
      const features = await ApiRouter.dbGateway.searchByLngLat(lngNum, latNum);
      let articles: DbArticle[] = []
      if (features.length > 0){ // found feature (should always be the case)
        articles = features[0].articles
      }
      return createApiSuccessResponse<DbArticle[]>(articles);
    } catch (error: any){
      return createApiFailureResponse('error', 500, 
        error instanceof Error ? error.message : 'Unknown error')
    }
  }

  private async handleSearch(req: Request, res: Response): Promise<ApiResponse<DbArticle[]>>{
    const input = req.params.input;

    if (!input || input.trim().length === 0){
      return createApiFailureResponse('error_bad_request', 400, 
        "Must specify an input for your search");
    }

    try {
      // TODO: can also specify a from date, to date, and bbox to the method
      const articles = await ApiRouter.dbGateway.searchArticlesByKeywords(input);
      return createApiSuccessResponse<DbArticle[]>(articles);
    } catch (error: any){
      return createApiFailureResponse('error', 500, 
        error instanceof Error ? error.message : 'Unknown error')
    }
  }

  private async handleDateRangeRequest(req: Request, res: Response): Promise<ApiResponse<DbArticle[]>>{
    const fromDate: string = req.params.fromDate;
    const toDate: string = req.params.toDate;

    try {
      const articles = await ApiRouter.articleDateRangeHandler.searchArticlesByDateRange(fromDate, toDate);
      return createApiSuccessResponse<DbArticle[]>(articles);
    } catch (error: any){
      return createApiFailureResponse('error', 500, 
        error instanceof Error ? error.message : 'Unknown error')
    }
  }

  getExpressRouter = (): Router => {
    return this.router;
  } 
}

