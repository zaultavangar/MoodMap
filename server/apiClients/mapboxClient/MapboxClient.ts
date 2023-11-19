import { ApiResponse } from "../../apiResponses";
import { AugmentedFeature } from "../../types";

export interface MapboxClient{
  // change return type to list of features
  handleGeocodeRequest(location: string): Promise<ApiResponse<AugmentedFeature>>;
}