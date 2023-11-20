import mongoose, { Types } from "mongoose";
import { AugmentedFeature, GuardianArticle } from "./types";
import { DbFeature, FeatureModel } from "./models/Feature";
import { ArticleModel, DbArticle } from "./models/Article";
import { Bbox } from "./Bbox";
import { ArticleDateRangeHandler } from "./ArticleDateRangeHandler";

// TODO: ERROR HANDLING

export class DbGateway implements ArticleDateRangeHandler{
  
  // ADDS A FEATURE TO THE DB 
  addOrUpdateFeature = async (feature: AugmentedFeature, articleIds: Types.ObjectId[]): Promise<DbFeature> => {
    const {place_type, relevance, center, context, ...dbFeature} = feature;
    const updatedFeature =  await FeatureModel.findOneAndUpdate(
      {placeName: dbFeature.place_name },
      {$push: {articles: {$each: articleIds}}},
      {returnDocument: 'after', upsert: true},
    );
    return updatedFeature;
  }

  // ADDS AND ARTICLE TO THE DB
  addArticle = async (article: GuardianArticle, sentimentScore: number): Promise<DbArticle> => {
    const newArticle =  new ArticleModel({
      headline: article.webTitle,
      thumbnail: article.fields.thumbnail,
      url: article.webUrl,
      publicationDate: article.webPublicationDate,
      bodyText: article.fields.bodyText,
      sentimentScore: sentimentScore
    });
    return await newArticle.save();
  }

  // RETURNS A LIST OF ARTICLES (WILL GO THROUGH CACHE FIRST)
  searchArticlesByDateRange = async (fromDate: string, toDate: string): Promise<DbArticle[]> => {
    const results = await ArticleModel.find({
      publicationDate: { $gte: fromDate, $lte: toDate }
    });
    return results;
  }

  // RETURNS ARTICLES MATCHING; CAN SPECIFY RANGE OF DATES OR BBOX
  searchArticlesByKeywords = async (
    input: string, 
    fromDate?: Date, 
    toDate?: Date, 
    bbox?: Bbox): Promise<DbArticle[]> => {

    const articleQuery: any = {};

    // Text search
    if (input) articleQuery.$text = { $search: input };
    
    // Date filtering
    if (fromDate && toDate) {
      articleQuery.publicationDate = { $gte: fromDate, $lte: toDate };
    } else if (fromDate) {
      articleQuery.publicationDate = { $gte: fromDate };
    } else if (toDate) {
      articleQuery.publicationDate = { $lte: toDate };
    }

    let articleIds: Types.ObjectId[] = [];

    // Features iwthin bbox
    if (bbox) {
      const features = await FeatureModel.find({
        geometry: {
          $geoWithin: {
            $box: [[bbox.left, bbox.bottom], [bbox.right, bbox.top]]
          }
        }
      });

      // Extract article IDs from the features
      articleIds = features.flatMap(feature => feature.articles);
    }

    // If bbox is provided, filter articles by the IDs found, otherwise use the text and date filters
    const articles = bbox 
      ? await ArticleModel.find({ ...articleQuery, _id: { $in: articleIds } })
      : await ArticleModel.find(articleQuery);

    return articles; // TODO: Maybe sort
  }

  // RETURNS TOP FEATURES POPULATED W ARTICLE INFO (MAY WANT TO RETURN MORE THAN ONE FEATURE LATER)
  searchByLngLat = async (lng: number, lat: number, fromDate?: Date, toDate?: Date): Promise<DbFeature[]> => {
    // TODO: maybe just return one location
    const feature = await FeatureModel.aggregate([
      {
        $geoNear : {
          near: {type: "Point", coordinates: [lng, lat]},
          distanceField: "dist.calculated",
        },
        $limit: 1
      }
    ]);
    let matchCondition = {};
    if (fromDate && toDate) {
      matchCondition = {
        publicationDate: { $gte: fromDate, $lte: toDate }
      };
    } 
    const populatedResult = await FeatureModel.populate(feature, {
      path: 'article',
      match: matchCondition
    })
    // returns list of features with all articles nested
    return populatedResult; 
  }

}