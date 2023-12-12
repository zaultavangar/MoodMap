import Grid from "@mui/material/Unstable_Grid2";
import Searchbar from "./Searchbar";
import SearchResults from "./SearchResults";
import { useRecoilValue } from "recoil";
import { searchQueryState, searchResultsState } from "~/atoms";
const Header = () => {

  // const {selectedDateRange, setSelectedDateRange} = appControl;

  const searchQuery = useRecoilValue(searchQueryState);
  const searchResults = useRecoilValue(searchResultsState);

  const canDisplaySearchResults = searchQuery !== "" && searchResults.length > 0;

  return (
    <div className="header-container">
      <Searchbar/>
    </div>
      // <Grid
      //   container
      //   xs='auto'
      //   component="header"
      //   rowSpacing={2}
      //   sx={{
      //     zIndex: 4,
      //     flexDirection: "column",
      //     position: "relative",
      //     padding: "1rem",
      //     flexGrow: 1,
      //   }}
      // >
      //   <Grid xs={12} md={6}>
      //     <Searchbar />
      //   </Grid>
      //   {canDisplaySearchResults && (
      //     <Grid xs={12} md={6}>
      //       <SearchResults />
      //     </Grid>
      //   )}
      // </Grid>
  );
};

export default Header;
