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
import { searchDateFilterPopoverAnchorElState, searchQueryState } from "~/atoms";

const MenuButton = () => {
  return (
    <IconButton type="button" aria-label="menu">
      <MenuIcon />
    </IconButton>
  );
};

const SearchButton = () => {
  return (
    <IconButton type="button" aria-label="search">
      <SearchIcon />
    </IconButton>
  );
};

const DateFilterButton = ({
  onClick,
}: {
  onClick: (event: MouseEvent<HTMLButtonElement>) => void;
}) => {
  return (
    <IconButton type="button" aria-label="filter by year" onClick={onClick}>
      <ScheduleIcon />
    </IconButton>
  );
};

const DateFilterDialog = ({
  anchorEl,
  open,
  onClose,
}: {
  anchorEl: HTMLButtonElement | null;
  open: boolean;
  onClose: () => void;
}) => {
  return (
    <Popover
      open={open}
      data-testid="date-filter-dialog"
      onClose={onClose}
      anchorEl={anchorEl}
      sx={{ width: "600px", p: "4px 4px" }}
      anchorOrigin={{
        vertical: "top",
        horizontal: "right",
      }}
      transformOrigin={{
        vertical: "top",
        horizontal: "left",
      }}
    >
      <Paper elevation={2} sx={{ width: "300px", p: "4px 16px" }}>
        <Typography variant="h6" fontWeight="bold">
          Filter by Year
        </Typography>
        <Divider sx={{ marginBottom: "2rem" }} />
        <Slider
          sx={{ width: "100%" }}
          aria-label="year filter slider"
          defaultValue={2023}
          // getAriaValueText={valuetext}
          valueLabelDisplay="on"
          step={1}
          marks
          min={2003}
          max={2023}
        />
      </Paper>
    </Popover>
  );
};

/**
 * Searchbar component allows users to search news articles based on a given keyword
 */
const Searchbar = () => {
  const [searchQuery, setSearchQuery] = useRecoilState(searchQueryState);

  const [searchDateFilterPopoverAnchorEl, setSearchDateFilterPopoverAnchorEl] = useRecoilState(searchDateFilterPopoverAnchorElState);

  // const [anchorEl, setAnchorEl] = useState<HTMLButtonElement | null>(null);

  const handleClick = (event: React.MouseEvent<HTMLButtonElement>) => {
    setSearchDateFilterPopoverAnchorEl(event.currentTarget);
  };

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchQuery(e.target.value);
  }

  const open = Boolean(searchDateFilterPopoverAnchorEl);

  const handleClose = () => {
    setSearchDateFilterPopoverAnchorEl(null);
  };


  return (
    <>
      <Paper
        component="form"
        elevation={2}
        sx={{ p: "2px 4px", display: "flex", alignItems: "center" }}
        data-testid="searchbar"
      >
        <MenuButton /> 
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
        {<DateFilterButton onClick={handleClick} />}
      </Paper>
     <DateFilterDialog anchorEl={searchDateFilterPopoverAnchorEl} open={open} onClose={handleClose} />
    </>
  );
};

export default Searchbar;
