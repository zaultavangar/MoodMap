import { GeoJsonProperties, Geometry } from "geojson";

export type ArticleEntity = {
  webTitle: string;
  webUrl: string;
  sentimentScore: number;
  associatedLocation: string[];
  thumbnail: string;
  //url: string;
};

export type FeatureEntity = {
  type: "Feature";
  geometry: Geometry;
  properties: GeoJsonProperties;
};

export type MonthDateInfo = {
  selected: boolean,
  month: number
}

export type StatsOverviewMap = {
  mostMentioned: LocationToDetailsMap[]
  mostPositive: LocationToDetailsMap[]
  mostNegative: LocationToDetailsMap[]
};

export type LocationToDetailsMap = {
  [key: string]: LocationStats;
}

export type LocationStats = {
  count: number
  sentiment: number
  coordinates: number[]
};