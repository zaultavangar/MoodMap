import Grid from "@mui/material/Unstable_Grid2";
import Searchbar from "./Searchbar";
const Header = () => {
  return (
    <Grid
      container
      sx={{
        zIndex: 4,
        position: "relative",
        padding: "1rem",
        flexGrow: 1,
      }}
    >
      <Grid xs={12} md={6}>
        <Searchbar />
      </Grid>
    </Grid>
  );
};

export default Header;
