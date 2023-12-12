import {
  List,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Paper,
} from "@mui/material";
import NewspaperIcon from "@mui/icons-material/Newspaper";
import { useRecoilState, useRecoilValue } from "recoil";
import { searchQueryState, searchResultsState } from "~/atoms";
import { useEffect } from "react";
import { keywords } from "~/data/keywords";

const SearchResults = () => {

  const searchQuery = useRecoilValue(searchQueryState);
  const [searchResults, setSearchResults] = useRecoilState(searchResultsState);

  useEffect(() => {
    // TODO: Change this to async call 
    const foundKeywords = keywords.filter((keyword) =>
      keyword.toLowerCase().includes(searchQuery.toLowerCase())
    );
    setSearchResults(foundKeywords);
  }, [searchQuery]);

  return (
    <Paper elevation={2}>
      <List dense={false}>
        {searchResults.map((result, index) => (
          <ListItemButton key={index}>
            <ListItemIcon>
              <NewspaperIcon />
            </ListItemIcon>
            <ListItemText primary={result} />
          </ListItemButton>
        ))}
      </List>
    </Paper>
  );
};

export default SearchResults;
