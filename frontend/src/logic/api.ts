import axios from "axios";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

export type ArticleEntity = {
  // Define the structure of ArticleEntity based on your Java backend
};

type RestApiResponse<T> =
  | {
      articles: T;
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
};

export type Endpoint = keyof EndPoints;

export const handleApiResponse = async <T extends Endpoint>(
  endpoint: T,
  queryParams: EndPoints[T]
): Promise<RestApiResponse<ArticleEntity[]>> => {
  return axios
    .get<ArticleEntity[]>(`${API_BASE_URL}/${endpoint}`, {
      params: { ...queryParams },
      validateStatus: (status) => status !== 500 && status !== 400,
    })
    .then((response) => ({
      articles: response.data,
    }))
    .catch((error) =>
      error.response
        ? {
            message: error.response.data.message,
          }
        : {
            message: "Unknown error",
          }
    );
};
