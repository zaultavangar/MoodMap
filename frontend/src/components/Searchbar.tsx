import { Paper, InputBase, IconButton } from "@mui/material";
import SearchIcon from "@mui/icons-material/Search";
import MenuIcon from "@mui/icons-material/Menu";

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
    </Paper>
  );
};

export default Searchbar;
