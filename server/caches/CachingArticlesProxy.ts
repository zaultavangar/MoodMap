import NodeCache from "node-cache";
import { ArticleDateRangeHandler } from "../ArticleDateRangeHandler";
import { GuardianArticle } from "../types";
import { CachingProxy } from "./CachingProxy";
import { DbArticle } from "../models/Article";

export class CachingArticlesProxy extends CachingProxy<DbArticle[]> implements ArticleDateRangeHandler{
  private wrappedArticleDateRangeHandler: ArticleDateRangeHandler;

  constructor(dateRangeHandler: ArticleDateRangeHandler){
    super(0, 600, true);
    this.wrappedArticleDateRangeHandler = dateRangeHandler;
  }

  async handleRequest(fromDate: string, toDate: string): Promise<DbArticle[]> {
    const concatDates = fromDate.concat(toDate);
    const cachedArticles = this.cache.get<DbArticle[]>(concatDates);
    if (cachedArticles){
      return cachedArticles;
    }
    // if not found in cache, call the wrapped handler
    const fetchedArticles = await this.wrappedArticleDateRangeHandler.searchArticlesByDateRange(fromDate, toDate);
    this.cache.set<DbArticle[]>(concatDates, fetchedArticles);
    return fetchedArticles;
  }

  async searchArticlesByDateRange(fromDate: string, toDate: string): Promise<DbArticle[]> {
    return await this.handleRequest(fromDate, toDate);
  }


}