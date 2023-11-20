import mongoose from "mongoose"
import { Feature, FeatureCollection, GeoJsonObject, Geometry, GeoJsonProperties } from 'geojson';

// API RESPONSES


// GEOJSON RELATED
export interface AugmentedFeature<G extends Geometry | null = Geometry, P = GeoJsonProperties> extends Feature<G, P> {
    place_type: string[];
    relevance: number;
    text: string;
    place_name: string;
    center: number[];
    context: {[key: string]: string}[];
}
export interface AugmentedFeatureCollection<G extends Geometry | null = Geometry, P = GeoJsonProperties> extends FeatureCollection<G, P> {
    features: Array<AugmentedFeature<G, P>>;
}



// BERT SENTIMENT
export type BertSentiment = {
    label: string
    score: number
}

// GUARDIAN RELATED
export type GuardianResponse = {
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

export type GuardianArticle = {
    id: string
    type: string
    sectionId: string
    sectionName: string
    webPublicationDate: string
    webTitle: string
    webUrl: string
    apiUrl: string
    fields: GuardianArticleFields
}

export type GuardianArticleFields = {
    trailText: string
    thumbnail: string
    bodyText: string
}

export type NerResponse = {
    entities: NerEntity[]   
}

export type NerEntity = {
    entity_group: string
    score: number
    word: string
    start: number
    end: number
}

export type SentimentResponse = {
   data: {
    sentimentScore: number
   } 
}

export type FeatureToFeatureInfo = {
    self: GeoJSON.Feature,
    parent: GeoJSON.Feature
}





