import MenuIcon from "@mui/icons-material/Menu";
import ScheduleIcon from "@mui/icons-material/Schedule";
import SearchIcon from "@mui/icons-material/Search";
import {
  Divider,
  IconButton,
  InputBase,
  Paper,
  Popover,
  Slider,
  Typography,
} from "@mui/material";
import React, { MouseEvent } from "react";
import { useRecoilState, useRecoilValue } from "recoil";
import {
  searchDateFilterPopoverAnchorElState,
  searchQueryState,
} from "~/atoms";

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
  const [searchQuery, setSearchQuery] = useRecoilState(searchQueryState);

  const [searchDateFilterPopoverAnchorEl, setSearchDateFilterPopoverAnchorEl] =
    useRecoilState(searchDateFilterPopoverAnchorElState);

  // const [anchorEl, setAnchorEl] = useState<HTMLButtonElement | null>(null);

  const handleClick = (event: React.MouseEvent<HTMLButtonElement>) => {
    setSearchDateFilterPopoverAnchorEl(event.currentTarget);
  };

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchQuery(e.target.value);
  };

  const open = Boolean(searchDateFilterPopoverAnchorEl);

  const handleClose = () => {
    setSearchDateFilterPopoverAnchorEl(null);
  };

  return (
    <Paper
      component="form"
      elevation={2}
      sx={{ p: "2px 4px", display: "flex", alignItems: "center" }}
      data-testid="searchbar"
    >
      {/* Input field to search for keyword */}
      <InputBase
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
