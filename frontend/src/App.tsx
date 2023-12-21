// import { ThemeProvider, createTheme } from "@mui/material/styles";
import React from "react";
import Map from "~/components/Map";
import Header from "~/components/Header";
import { DatePicker } from "./components/datepicker/DatePicker";
import { RecoilRoot } from "recoil";
/**
 * App component is the root component for the web app
 */
const App = () => {
  return (
    <>
      {/**RecoilRoot is necessary to share state management context between hooks and components */}
      <RecoilRoot>
        <Header />

        <Map />
        <DatePicker />
      </RecoilRoot>
    </>
  );
};

export default App;
