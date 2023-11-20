import mongoose, { Types } from "mongoose";

const Schema = mongoose.Schema;

export interface DbArticle {
  _id: Types.ObjectId;
  headline: string,
  thumbnail: string,
  url: string,
  publicationDate: Date,
  bodyText: string,
  sentimentScore: number
}

const articleSchema = new Schema<DbArticle>({
  _id: Types.ObjectId,
  headline: String,
  thumbnail: String,
  url: String,
  publicationDate: Date,
  bodyText: String,
  sentimentScore: Number,
})

articleSchema.index({headline: 'text'});
export const ArticleModel = mongoose.model('Article', articleSchema);

