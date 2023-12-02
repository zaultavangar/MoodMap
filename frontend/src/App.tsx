// import { ThemeProvider, createTheme } from "@mui/material/styles";
import Map from "~/components/Map";
import Header from "~/components/Header";
import { DatePicker } from "./components/datepicker/DatePicker";
import { useState } from "react";

// const theme = createTheme({
//   palette: {
//     mode: "dark"
//   }
// })

const App = () => {



  return (
    <>
      {/* <ThemeProvider theme={theme}>
        <Header />
        <Map />
      </ThemeProvider> */}

      <Header/>

      <Map />
    </>
  );
};

export default App;
