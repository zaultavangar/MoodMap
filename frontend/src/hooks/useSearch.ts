import { useRecoilValue, useSetRecoilState } from "recoil";
import { searchByDateRangeState, searchQueryState, searchResultsState, selectedMonthState, selectedYearState } from "~/atoms";
import { FrontendApiResponse, handleApiResponse, isSuccessfulResponse } from "~/logic/api"
import { ArticleEntity } from "~/types";

export function useSearch() {

  const setSearchResults = useSetRecoilState(searchResultsState);
  const setSearchQuery = useSetRecoilState(searchQueryState);

  const searchByDateRange = useRecoilValue(searchByDateRangeState);
  const selectedMonth = useRecoilValue(selectedMonthState);
  const selectedYear = useRecoilValue(selectedYearState);

  const search = async (input: string) => {
    let res: FrontendApiResponse<ArticleEntity[]>; 
    if (searchByDateRange) {
      const formattedMonth = selectedMonth ? selectedMonth.toString().padStart(2, '0') : '';
      const formattedYear = selectedYear.toString();
      const lastDayOfMonth = new Date(selectedYear, selectedMonth, 0).getDate();

      res = await handleApiResponse<"search", ArticleEntity[]>(
        "search",
        {
          input: input,
          fromDate: `${formattedYear}-${formattedMonth}-01`,
          toDate: `${formattedYear}-${formattedMonth}-${lastDayOfMonth.toString()}`
        }
      )
    } else {
      res = await handleApiResponse<"search", ArticleEntity[]>(
        "search",
        {input: input}
      );
    }
    
    console.error(res);
    if (isSuccessfulResponse(res)){
      setSearchResults(res.data);
    }
  }

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchQuery(e.target.value);
  };


  return { search, handleSearchChange }
}