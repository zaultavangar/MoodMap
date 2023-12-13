import { useSetRecoilState } from "recoil";
import { searchQueryState, searchResultsState } from "~/atoms";
import { handleApiResponse, isSuccessfulResponse } from "~/logic/api"
import { ArticleEntity } from "~/types";

export function useSearch() {

  const setSearchResults = useSetRecoilState(searchResultsState);
  const setSearchQuery = useSetRecoilState(searchQueryState);

  const search = async (input: string) => {
    const res = await handleApiResponse<"search", ArticleEntity[]>(
      "search",
      {input: input}
    );
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