import axios, { AxiosResponse } from 'axios';
import { GeoJsonProperties, Geometry } from 'geojson';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

export type ArticleEntity = {
  webTitle: string
  webUrl: string,
  sentimentScore: number
  associatedLocation: string[]
  //url: string;
};

export type FeatureEntity = {
  type: 'Feature';
  geometry: Geometry
  properties: GeoJsonProperties
}

export type ApiResponse<T> = {
  timestamp: string;
  status: number;
  result: string;
  data: T;
};

export type FrontendApiResponse<T> =
  | {
      data: T;
    }
  | { message: string };

type InputParams = {
  input: string;
};

type LocationParams = {
  location: string;
};

type DateRangeParams = {
  fromDate: string;
  toDate: string;
};

type UniqueKeys<T> = T extends Record<string, any>
  ? keyof T extends `${infer K}`
    ? K extends `${infer First}${string & K}`
      ? First extends Uppercase<First>
        ? never
        : K
      : never
    : never
  : never;

type ExclusiveUnion<T, U, V> = T | (U & V);

type SearchDateWithParams<T> = ExclusiveUnion<
  T,
  Record<UniqueKeys<T>, never>,
  DateRangeParams
>;

type SearchArticlesParams = SearchDateWithParams<InputParams>;
type SearchLocationParams = SearchDateWithParams<LocationParams>;

type EndPoints = {
  search: SearchArticlesParams;
  searchByLocation: SearchLocationParams;
  searchByDateRange: DateRangeParams;
  getFeatures: {};
};

export type Endpoint = keyof EndPoints;

export const handleApiResponse = async <T extends Endpoint, K>(
  endpoint: T,
  queryParams: EndPoints[T]
): Promise<FrontendApiResponse<K>> => {
  return axios
    .get<AxiosResponse<ApiResponse<K>>, AxiosResponse<ApiResponse<K>>>(`${API_BASE_URL}/${endpoint}`, {
      params: { ...queryParams },
      validateStatus: (status) => status !== 500 && status !== 400,
    })
    .then((response) => {
      console.error("Response", response)
      return {
      
      data: response.data.data
    
    }})
    .catch((error) =>
      error.response
        ? {
            message: error.response.data.result,
          }
        : {
            message: "Unknown error",
          }
    );
};

export const isSuccessfulResponse = <T>(response: FrontendApiResponse<T>): response is { data: T } => {
  return 'data' in response;
};
