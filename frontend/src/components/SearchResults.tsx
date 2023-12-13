import React, { useEffect } from "react";
import {
  List,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Paper,
} from "@mui/material";
import NewspaperIcon from "@mui/icons-material/Newspaper";
import { useRecoilValue } from "recoil";
import { searchQueryState, searchResultsState  } from "~/atoms";
import { useSearch } from "~/hooks/useSearch";

const SearchResults = () => {

  const searchQuery = useRecoilValue(searchQueryState);
  const searchResults = useRecoilValue(searchResultsState);

  const {search} = useSearch();
  useEffect(() => {
    search(searchQuery);
  }, [searchQuery]);

  // TODO: Make search results scrollable when it goes past the viewport height
  // TODO: Format search results similarly to popup, i.e. with article thumbnail
  // TODO: Maybe include a button next to each result that will fly them to the location?

  return (
    <Paper elevation={2} >
      <List dense={false}>
        {searchResults.map((result, index) => (
          <ListItemButton key={index}>
            <ListItemIcon>
              <NewspaperIcon />
            </ListItemIcon>
            <ListItemText primary={result.webTitle} />
          </ListItemButton>
        ))}
      </List>
    </Paper>
  );
};

export default SearchResults;
