import React from "react";
import SearchIcon from "@mui/icons-material/Search";
import DateRangeIcon from "@mui/icons-material/DateRange";
import {
  Box,
  Checkbox,
  FormControlLabel,
  IconButton,
  InputBase,
  Paper,
  Stack,
} from "@mui/material";
import { useRecoilState, useRecoilValue, useSetRecoilState } from "recoil";
import {
  searchByDateRangeState,
  searchDateRangeOptionOpenState,
  searchQueryState,
  searchResultsState,
} from "~/atoms";
import { useSearch } from "~/hooks/useSearch";
import "./Searchbar.css";

/**
 * Searchbar component allows users to search news articles based on a given keyword
 */
const Searchbar = () => {
  const [searchQuery, setSearchQuery] = useRecoilState(searchQueryState);
  const setSearchResults = useSetRecoilState(searchResultsState);
  const [searchDateRangeOptionOpen, setSearchDateRangeOptionOpen] =
    useRecoilState(searchDateRangeOptionOpenState);
  const [searchByDateRange, setSearchByDateRange] = useRecoilState(
    searchByDateRangeState
  );

  const { handleSearchChange } = useSearch();

  const onDateRangeClick = (e: React.MouseEventHandler<HTMLButtonElement>) => {
    setSearchDateRangeOptionOpen(true);
  };

  const DateRangeOptionButton = () => {
    return (
      <IconButton
        type="button"
        aria-label="search"
        id="search-date-range-button"
        data-testid="search-date-range-button"
        style={{ marginRight: "5px" }}
        onClick={() => setSearchDateRangeOptionOpen(!searchDateRangeOptionOpen)}
      >
        <DateRangeIcon />
      </IconButton>
    );
  };

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
    <Stack direction={"column"} spacing={1}>
      <Paper
        component="form"
        elevation={2}
        sx={{ p: "2px 4px", display: "flex", alignItems: "center" }}
        data-testid="searchbar"
      >
        <DateRangeOptionButton />
        {/* Input field to search for keyword */}
        <InputBase
          autoComplete="off"
          sx={{ ml: 1, flex: 1 }}
          id="outlined-basic"
          placeholder="Search the news by keyword"
          inputProps={{
            "aria-label": "search the news by keyword",
            "data-testid": "searchbar-input",
          }}
          value={searchQuery}
          onChange={handleSearchChange}
        />
      </Paper>
      {searchDateRangeOptionOpen && (
        <Paper className="search-date-range-option-container">
          <FormControlLabel
            control={
              <Checkbox
                id="search-date-range-option-checkbox"
                data-test-id="search-date-range-option-checkbox"
                onClick={() => {
                  setSearchByDateRange(!searchByDateRange);
                }}
                checked={searchByDateRange}
              />
            }
            label="Search by currently selected date range?"
            labelPlacement="start"
          />
        </Paper>
      )}
    </Stack>
  );
};

export default Searchbar;
