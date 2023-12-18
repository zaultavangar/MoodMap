import axios, { AxiosResponse } from "axios";
import { ArticleEntity, FeatureEntity } from "~/types";
//@ts-ignore
import mockFeaturesResponse from "../../__mocks__/features.json";
//@ts-ignore
import mockLocationResponse from "../../__mocks__/location.json";
//@ts-ignore
import mockDateRangeResponse from "../../__mocks__/dateRange.json";
//@ts-ignore
import mockKeywordResponse from "../../__mocks__/keyword.json";
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

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

interface API {
  getFeatures(): Promise<FrontendApiResponse<FeatureEntity[]>>;
  search(
    input: string,
    fromDate?: string,
    toDate?: string
  ): Promise<FrontendApiResponse<ArticleEntity[]>>;
  searchByLocation(
    location: string,
    fromDate: string,
    toDate: string
  ): Promise<FrontendApiResponse<ArticleEntity[]>>;
}

class MockAPI implements API {
  search(
    input: string,
    fromDate?: string,
    toDate?: string
  ): Promise<FrontendApiResponse<ArticleEntity[]>> {
    return Promise.resolve({
      data: mockKeywordResponse.data,
    });
  }
  getFeatures(): Promise<FrontendApiResponse<FeatureEntity[]>> {
    return Promise.resolve({
      data: mockFeaturesResponse.data,
    });
  }
  searchByLocation(
    location: string,
    fromDate: string,
    toDate: string
  ): Promise<FrontendApiResponse<ArticleEntity[]>> {
    return Promise.resolve({
      data: mockLocationResponse.data,
    });
  }
}

export class RealAPI implements API {
  search(
    input: string,
    fromDate?: string,
    toDate?: string
  ): Promise<FrontendApiResponse<ArticleEntity[]>> {
    return axios
      .get(`${API_BASE_URL}/search`, {
        params: { input, fromDate, toDate },
        validateStatus: (status) => status !== 500 && status !== 400,
      })
      .then((response) => {
        // console.error("Response", response);
        return {
          data: response.data.data,
        };
      })
      .catch((error) =>
        error
          ? {
              message: error.result,
            }
          : {
              message: "Unknown error",
            }
      );
  }
  getFeatures(): Promise<FrontendApiResponse<FeatureEntity[]>> {
    return axios
      .get(`${API_BASE_URL}/getFeatures`, {
        validateStatus: (status) => status !== 500 && status !== 400,
      })
      .then((response) => {
        // console.error("Response", response);
        return {
          data: response.data.data,
        };
      })
      .catch((error) =>
        error
          ? {
              message: error.result,
            }
          : {
              message: "Unknown error",
            }
      );
  }
  searchByLocation(
    location: string,
    fromDate: string,
    toDate: string
  ): Promise<FrontendApiResponse<ArticleEntity[]>> {
    return axios
      .get(`${API_BASE_URL}/searchByLocation`, {
        params: { location, fromDate, toDate },
        validateStatus: (status) => status !== 500 && status !== 400,
      })
      .then((response) => {
        // console.error("Response", response);
        return {
          data: response.data,
        };
      })
      .catch((error) =>
        error
          ? {
              message: error.result,
            }
          : {
              message: "Unknown error",
            }
      );
  }
}

// export const handleApiResponse = async <T extends Endpoint, K>(
//   endpoint: T,
//   queryParams: EndPoints[T]
// ): Promise<FrontendApiResponse<K>> => {
//   return axios
//     .get<AxiosResponse<ApiResponse<K>>, AxiosResponse<ApiResponse<K>>>(
//       `${API_BASE_URL}/${endpoint}`,
//       {
//         params: { ...queryParams },
//         validateStatus: (status) => status !== 500 && status !== 400,
//       }
//     )
//     .then((response) => {
//       console.error("Response", response);
//       return {
//         data: response.data.data,
//       };
//     })
//     .catch((error) =>
//       error.response
//         ? {
//             message: error.response.data.result,
//           }
//         : {
//             message: "Unknown error",
//           }
//     );
// };

export const isSuccessfulResponse = <T>(
  response: FrontendApiResponse<T>
): response is { data: T } => {
  return "data" in response;
};

// const api = import.meta.env.MODE === "dev" ? new MockAPI() : new RealAPI();
const api = new RealAPI();

export default api;
