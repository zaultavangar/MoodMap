import mongoose, {Types, Document, PopulatedDoc} from "mongoose";
import { DbArticle } from "./Article";

const Schema = mongoose.Schema;

// https://mongoosejs.com/docs/5.x/docs/typescript/populate.html

export interface DbFeature {
  _id: Types.ObjectId;
  type: string;
  properties: any; // adjust the type as needed
  geometry: {
    type: string;
    coordinates: Types.Array<number>;
  };
  text: string;
  placeName: string;
  articles: PopulatedDoc<DbArticle & Document>[]
}

const featureSchema = new Schema<DbFeature>({
  _id: Types.ObjectId,
  type: {
    type: String,
    required: true,
    enum: ['Feature'] // GeoJSON Feature objects have a type of "Feature"
  },
  properties: {
  type: Schema.Types.Mixed,
   default: {}
  },
  geometry: {
    type: {
      type: String,
      required: true,
      enum: ['Point'] 
    },
    coordinates: {
      type: [Number],
      required: true
    }
  },
  text: String,
  placeName: String,
  articles: [{type: Types.ObjectId, ref: 'Article'}]
});

// create 2dsphere for index to simplify geosphere queries  
featureSchema.index({geometry: "2dsphere"});
featureSchema.index({placeName: 1}, {unique: true});

export const FeatureModel = mongoose.model('Feature', featureSchema);

