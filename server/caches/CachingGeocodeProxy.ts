import NodeCache from "node-cache";
import { MapboxClient } from "../apiClients/mapboxClient/MapboxClient";
import { ApiResponse, createApiSuccessResponse, isApiSuccessResponse } from "../apiResponses";
import { AugmentedFeature } from "../types";
import { CachingProxy } from "./CachingProxy";

export class CachingGeocodeProxy extends CachingProxy<ApiResponse<AugmentedFeature>> implements MapboxClient {
  private wrappedMapboxClient: MapboxClient;

  constructor(mapboxClient: MapboxClient){
    super(0, 600, true);
    this.wrappedMapboxClient = mapboxClient;
  }

  async handleRequest(location: string): Promise<ApiResponse<AugmentedFeature>> {
    const cachedFeature = this.cache.get<AugmentedFeature>(location);
    if (cachedFeature){
      return createApiSuccessResponse<AugmentedFeature>(cachedFeature);
    }
    // if not found in cache, call the wrapped client
    const res = await this.wrappedMapboxClient.handleGeocodeRequest(location);
    if (isApiSuccessResponse(res)){
      this.cache.set<AugmentedFeature>(location, res.data);
    } 
    return res;
  }

  async handleGeocodeRequest(location: string): Promise<ApiResponse<AugmentedFeature>> {
    return await this.handleRequest(location);
  }


}