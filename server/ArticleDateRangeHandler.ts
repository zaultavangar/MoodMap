import { DbArticle } from "./models/Article";
import { GuardianArticle } from "./types";

export interface ArticleDateRangeHandler{
  searchArticlesByDateRange(fromDate: string, toDate: string): Promise<DbArticle[]>;
}