export type ApiSuccessResponse<T> = {
  result: 'success';
  statusCode: number;
  data: T;
}

export type ApiFailureResponse = {
  result: string;
  statusCode: number;
  error_message: string;
}

export type ApiResponse<T> = ApiSuccessResponse<T> | ApiFailureResponse;

export const createApiSuccessResponse = <T>(data: T): ApiSuccessResponse<T> => {
  return {
    result: 'success',
    statusCode: 200,
    data: data
  }
}

export const createApiFailureResponse = (result: string, statusCode: number, error_message: string) : ApiFailureResponse => {
  return {
    result,
    statusCode,
    error_message
  }
}

export const isApiSuccessResponse = <T>(res: ApiResponse<T>): res is ApiSuccessResponse<T> => {
  return res && res.result === 'success' && 'data' in res;
} 