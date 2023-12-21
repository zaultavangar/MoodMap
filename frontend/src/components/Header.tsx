import React from "react";
import Grid from "@mui/material/Unstable_Grid2";
import Searchbar from "./searchbar/Searchbar";
import SearchResults from "./SearchResults";
import { useRecoilValue } from "recoil";
import { searchQueryState } from "~/atoms";
import { Stack } from "@mui/material";

/**
 * Header component helps layout and display the Searchbar and SearchResults components
 */
const Header = () => {
  const searchQuery = useRecoilValue(searchQueryState);

  return (
    <Stack
      spacing={1}
      sx={{
        zIndex: 4,
        flexDirection: "column",
        position: "relative",
        padding: "1rem",
        flexGrow: 1,
        maxWidth: "50vw",
      }}
    >
      <Searchbar />
      {searchQuery.length > 0 && <SearchResults />}
    </Stack>
  );
};

export default Header;
