import React from 'react';
import SearchIcon from "@mui/icons-material/Search";
import {
  IconButton,
  InputBase,
  Paper,
} from "@mui/material";
import { useRecoilValue } from "recoil";
import {
  searchQueryState,
} from "~/atoms";
import { useSearch } from "~/hooks/useSearch";

const SearchButton = () => {
  return (
    <IconButton type="button" aria-label="search">
      <SearchIcon />
    </IconButton>
  );
};

/**
 * Searchbar component allows users to search news articles based on a given keyword
 */
const Searchbar = () => {
  const searchQuery = useRecoilValue(searchQueryState);

  const { handleSearchChange } = useSearch();

  // const [searchDateFilterPopoverAnchorEl, setSearchDateFilterPopoverAnchorEl] =
  //   useRecoilState(searchDateFilterPopoverAnchorElState);

  // const [anchorEl, setAnchorEl] = useState<HTMLButtonElement | null>(null);

  // const handleClick = (event: React.MouseEvent<HTMLButtonElement>) => {
  //   setSearchDateFilterPopoverAnchorEl(event.currentTarget);
  // };


  // const open = Boolean(searchDateFilterPopoverAnchorEl);

  // const handleClose = () => {
  //   setSearchDateFilterPopoverAnchorEl(null);
  // };

  return (
    <Paper
      component="form"
      elevation={2}
      sx={{ p: "2px 4px", display: "flex", alignItems: "center" }}
      data-testid="searchbar"
    >
      {/* Input field to search for keyword */}
      <InputBase
        autoComplete="off"
        sx={{ ml: 1, flex: 1 }}
        id="outlined-basic"
        placeholder="Search the news by keyword"
        inputProps={{ "aria-label": "search the news by keyword" }}
        value={searchQuery}
        onChange={handleSearchChange}
      />
      <SearchButton />
    </Paper>
  );
};

export default Searchbar;
