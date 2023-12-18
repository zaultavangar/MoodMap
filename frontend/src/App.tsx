// import { ThemeProvider, createTheme } from "@mui/material/styles";
import React from "react";
import Map from "~/components/Map";
import Header from "~/components/Header";
import { DatePicker } from "./components/datepicker/DatePicker";
import { RecoilRoot } from "recoil";
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
      <RecoilRoot>
        <Header />

        <Map />
        <DatePicker />
      </RecoilRoot>
    </>
  );
};

export default App;
