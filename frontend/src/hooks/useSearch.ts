import { useRecoilValue, useSetRecoilState } from "recoil";
import {
  searchByDateRangeState,
  searchQueryState,
  searchResultsState,
  selectedMonthState,
  selectedYearState,
} from "~/atoms";
import api from "~logic/api";
import { FrontendApiResponse, isSuccessfulResponse } from "~/logic/api";
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
      let startMonth: string;
      let endMonth: string;
      if (selectedMonth) {
        startMonth = endMonth = selectedMonth.toString().padStart(2, "0");
      } else {
        startMonth = "01";
        endMonth = "12";
      }
      const formattedYear = selectedYear.toString();
      const lastDayOfMonth = new Date(selectedYear, selectedMonth, 0).getDate();

      res = await api.search(
        input,
        `${formattedYear}-${startMonth}-01`,
        `${formattedYear}-${endMonth}-${lastDayOfMonth.toString()}`
      );
      // res = await handleApiResponse<"search", ArticleEntity[]>("search", {
      //   input: input,
      //   fromDate: `${formattedYear}-${startMonth}-01`,
      //   toDate: `${formattedYear}-${endMonth}-${lastDayOfMonth.toString()}`,
      // });
    } else {
      res = await api.search(input);
      // res = await handleApiResponse<"search", ArticleEntity[]>("search", {
      //   input: input,
      // });
    }

    // console.error(res.data);
    if (isSuccessfulResponse(res)) {
      setSearchResults(res.data);
    }
  };

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchQuery(e.target.value);
  };

  return { search, handleSearchChange };
}
