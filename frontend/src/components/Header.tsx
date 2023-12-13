import React from 'react';
import Grid from "@mui/material/Unstable_Grid2";
import Searchbar from "./Searchbar";
import SearchResults from "./SearchResults";
import { useRecoilValue } from "recoil";
import { searchQueryState } from "~/atoms";
const Header = () => {

  const searchQuery = useRecoilValue(searchQueryState);

  return (
    <Grid
      container
      xs="auto"
      component="header"
      rowSpacing={2}
      sx={{
        zIndex: 4,
        flexDirection: "column",
        position: "relative",
        padding: "1rem",
        flexGrow: 1,
      }}
    >
      <Grid xs={8} md={6}>
        <Searchbar />
      </Grid>
      {searchQuery !== "" && (
        <Grid xs={8} md={6}>
          <SearchResults />
        </Grid>
      )}
    </Grid>
  );
};

export default Header;
