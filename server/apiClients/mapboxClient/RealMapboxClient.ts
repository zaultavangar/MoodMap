import axios, { AxiosResponse } from "axios";
import { AugmentedFeature, AugmentedFeatureCollection} from "../../types";
import { MapboxClient } from "./MapboxClient";
import { ApiResponse, createApiFailureResponse, createApiSuccessResponse } from "../../apiResponses";

export class RealMapboxClient implements MapboxClient{
  private static MAPBOX_TOKEN = 'pk.eyJ1IjoiemF1bHRhdmFuZ2FyIiwiYSI6ImNsb2huMzgwcDE2M2oya3MxMWRqdWlsZnUifQ.Td7RlSdoMWoddaHFBj9G5g';

  async handleGeocodeRequest(location: string): Promise<ApiResponse<AugmentedFeature>> {
     // axios request to Mapbox Geocoding API
     // return first feature found 
     try {
      const axiosRes: AxiosResponse<AugmentedFeatureCollection, AugmentedFeatureCollection> = 
        await axios.get(`https://api.mapbox.com/geocoding/v5/mapbox.places/${location}.json`, {
          params: {
            access_token: RealMapboxClient.MAPBOX_TOKEN
          }
        });
      if (axiosRes.status === 200 && axiosRes.data){
        const featureCollection: AugmentedFeatureCollection = axiosRes.data;
        if (featureCollection.features && featureCollection.features.length > 0){
          return createApiSuccessResponse<AugmentedFeature>(featureCollection.features[0]);
        }
      }
      return createApiFailureResponse("error", axiosRes.status, `Failed to find a feature for location "${location}".`)
     } catch (error: any){
      return createApiFailureResponse("error", 500, 
        error instanceof Error ? error.message : 'Unknown error.');
     }
  } 
}