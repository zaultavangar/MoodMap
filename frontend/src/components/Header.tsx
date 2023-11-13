import Grid from "@mui/material/Unstable_Grid2";
import Searchbar from "./Searchbar";
import SearchResults from "./SearchResults";
import { useEffect, useState } from "react";
import { keywords } from "~/data/keywords";
import { Box } from "@mui/material";
const Header = () => {
  const [searchQuery, setSearchQuery] = useState("");
  const [results, setResults] = useState<string[]>([]);

  const handleSearchChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setSearchQuery(event.target.value);
  };

  useEffect(() => {
    const foundKeywords = keywords.filter((keyword) =>
      keyword.toLowerCase().includes(searchQuery.toLowerCase())
    );
    setResults(foundKeywords);
  }, [searchQuery]);

  return (
    <Grid
      container
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
      <Grid xs={12} md={6}>
        <Searchbar searchQuery={searchQuery} onChange={handleSearchChange} />
      </Grid>
      {searchQuery !== "" && results.length > 0 && (
        <Grid xs={12} md={6}>
          <SearchResults results={results} />
        </Grid>
      )}
    </Grid>
  );
};

export default Header;
