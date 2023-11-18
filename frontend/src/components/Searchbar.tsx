import MenuIcon from "@mui/icons-material/Menu";
import ScheduleIcon from "@mui/icons-material/Schedule";
import SearchIcon from "@mui/icons-material/Search";
import { IconButton, InputBase, Paper } from "@mui/material";

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

const DateFilterButton = () => {
  return (
    <IconButton type="button" aria-label="filter by year">
      <ScheduleIcon />
    </IconButton>
  );
};

/**
 * Searchbar component allows users to search news articles based on a given keyword
 */
const Searchbar = ({
  searchQuery,
  onChange,
}: {
  searchQuery: string;
  onChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
}) => {
  return (
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
        onChange={onChange}
      />
      <SearchButton />
      <DateFilterButton />
    </Paper>
  );
};

export default Searchbar;
