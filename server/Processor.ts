import { GuardianClient } from "./apiClients/guardianClient/GuardianClient";
import { MapboxClient } from "./apiClients/mapboxClient/MapboxClient";
import { NerClient } from "./apiClients/nerClient/NerClient";
import { SentimentClient } from "./apiClients/sentimentClient/SentimentClient";
import { ApiResponse, ApiSuccessResponse } from "./apiResponses";
import { AugmentedFeature, GuardianArticle, NerEntity } from "./types";

export class Processor{

  private sentimentClient: SentimentClient;
  private nerClient: NerClient;
  private guardianClient: GuardianClient;
  private mapboxClient: MapboxClient;

  constructor(sentimentClient: SentimentClient, nerClient: NerClient, 
    guardianClient: GuardianClient, mapboxClient: MapboxClient){
      this.sentimentClient = sentimentClient;
      this.nerClient = nerClient;
      this.guardianClient = guardianClient;
      this.mapboxClient = mapboxClient;
  }


  async processArticles(fromDate: string, toDate: string): Promise<void> {
    const res = await this.guardianClient.handleGuardianApiRequest(fromDate, toDate);
    if (!this.isGuardianClientSuccessResponse(res)){
      // throw new processing error
      return
    }
    const articles = res.data;
    for (const article of articles){
      this.processArticle(article);
    }
  }



  async processArticle(article: GuardianArticle) {
    if (article.type === 'liveblog') return null; // Skip liveblog articles

    // Destructure necessary article fields
    const { webPublicationDate, webTitle, webUrl, fields: { trailText, thumbnail, bodyText } } = article;
    
    // Perform sentiment analysis of the title and body text 
    const sentimentInput = webTitle.concat(bodyText);
    const sentimentRes = await this.sentimentClient.handleSentimentRequest(sentimentInput);
    if (!this.isSentimentClientSuccessResponse(sentimentRes)){
      // catch new processing error
      return
    }
    const sentimentScore: number = sentimentRes.data;  // Gets a sentiment score
    
    
    // Get location entities for the title, body, and trail text using NER

    /* Design choice (can change later): 512 max characters of input with NER 
      API so it just trims the input if it is too long */
    const nerInput = webTitle.concat(bodyText, trailText);
    const nerRes = await this.nerClient.handleNERRequest(nerInput);
    if (!this.isNerClientSuccessResponse(nerRes)){
      // throw new processing error
      return
    }
    const entities: NerEntity[] = nerRes.data;


    // transform locations entities into GeoJson features
    const features = await this.locationEntitiesToFeatures(entities); 
    // filter out features that don't make sense (look at turf)

    // Add article to DB 
    // Get articleId
    // Add each of the article's associated feature's the DB, passing in the retrieved articleId

  }

  async locationEntitiesToFeatures(entities: NerEntity[]){
    let features: AugmentedFeature[] = []
    // Get top feature for each entity 
    for (const entity of entities){
      if (entity.score > 0.8 && entity.word){
        const mapboxRes = await this.mapboxClient.handleGeocodeRequest(entity.word.trim());
        if (this.isMapboxClientSuccessResponse(mapboxRes)){
          const feature = mapboxRes.data;
          features.push(feature);
        }
      }
      console.log(`Low probability for ${entity.word}, left out of features list`)
    }
    return features;
  }

  isGuardianClientSuccessResponse(res: ApiResponse<GuardianArticle[]>): res is ApiSuccessResponse<GuardianArticle[]> {
    if (res.result === 'success') return true;
    return false;
  }

  isNerClientSuccessResponse(res: ApiResponse<NerEntity[]>): res is ApiSuccessResponse<NerEntity[]> {
    if (res.result === 'success') return true;
    return false;
  }

  isSentimentClientSuccessResponse(res: ApiResponse<number>): res is ApiSuccessResponse<number>{
    if (res.result === 'success') return true;
    return false;
  }
  
  isMapboxClientSuccessResponse(res: ApiResponse<AugmentedFeature>): res is ApiSuccessResponse<AugmentedFeature>{
    if (res.result === 'success') return true;
    return false;
  }
}