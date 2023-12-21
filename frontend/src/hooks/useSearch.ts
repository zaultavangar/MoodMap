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

/**
 * useSearch is a custom React hook that contains the logic for the Searchbar component.  Separating the logic from the component allows to more easily make changes to the view and to test the logic.
 */
export function useSearch() {
  const setSearchResults = useSetRecoilState(searchResultsState);
  const setSearchQuery = useSetRecoilState(searchQueryState);

  const searchByDateRange = useRecoilValue(searchByDateRangeState);
  const selectedMonth = useRecoilValue(selectedMonthState);
  const selectedYear = useRecoilValue(selectedYearState);

  /**
   * Debouncing introduces the ability to wait for a period of times before calling a function.
   * This is useful in the case of searching because it helps limit the amount of calls to the backend API, increasing performance on the frontend.
   */
  function debounce(func: Function, timeout = 300) {
    let debounceTimer;
    return (...args) => {
      clearTimeout(debounceTimer);
      debounceTimer = setTimeout(() => {
        func.apply(this, args);
      }, timeout);
    };
  }

  const search = debounce(async (input: string) => {
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
    } else {
      res = await api.search(input);
    }

    // console.error(res.data);
    if (isSuccessfulResponse(res)) {
      setSearchResults(res.data);
    }
  }, 300);

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchQuery(e.target.value);
  };

  return { search, handleSearchChange };
}
