import React from "react";
import "./YearPicker.css";
import NavigateBeforeIcon from "@mui/icons-material/NavigateBefore";
import NavigateNextIcon from "@mui/icons-material/NavigateNext";
import { useRecoilValue } from "recoil";
import { IconButton } from "@mui/material";
import { selectedYearState } from "~/atoms";
import { useDatePicker } from "~/hooks/useDatePicker";

const MIN_YEAR = 2019; // TODO: subject to change
const MAX_YEAR = new Date().getFullYear();

/**
 * YearPicker component allows the ability to switch the year on the date picker component
 */
export const YearPicker = () => {
  // State to track the current date

  const selectedYear = useRecoilValue(selectedYearState);

  // Disabling the buttons makes it useful to constrain the selectable years to a min and max year range
  const prevYearButtonDisabled: boolean = selectedYear == MIN_YEAR;
  const nextYearButtonDisabled: boolean = selectedYear == MAX_YEAR;

  const { nextYear, prevYear } = useDatePicker();

  return (
    <div className="year-container">
      <IconButton
        onClick={prevYear}
        data-testid="prev-year-button"
        disabled={prevYearButtonDisabled}
        className="navigation-button"
      >
        <NavigateBeforeIcon />
      </IconButton>
      <div id="current-date" data-testid="current-year">
        {selectedYear.toString()}
      </div>
      <IconButton
        onClick={nextYear}
        data-testid="next-year-button"
        disabled={nextYearButtonDisabled}
        className="navigation-button"
      >
        <NavigateNextIcon />
      </IconButton>
    </div>
  );
};
