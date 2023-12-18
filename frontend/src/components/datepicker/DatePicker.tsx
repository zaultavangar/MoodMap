import React from "react";
import { useEffect } from "react";
import "./DatePicker.css";
import { YearPicker } from "./yearPicker/YearPicker";
import { useRecoilValue } from "recoil";
import { isFullCalendarState, selectedMonthState } from "~/atoms";
import { MonthCalendarTable } from "./monthCalendarTable/MonthCalendarTable";
import { Checkbox, FormControlLabel } from "@mui/material";
import { useDatePicker } from "~/hooks/useDatePicker";

export const DatePicker = () => {
  // control date from here or from a useDatePicker
  // should be on the right hand side of the page

  // to toggle between full calendar and collapsed view
  const isFullCalendar = useRecoilValue(isFullCalendarState);
  const selectedMonth = useRecoilValue(selectedMonthState);

  const { handleResize, getIcon, onSelectMonthChange } = useDatePicker();

  useEffect(() => {
    handleResize();
    window.addEventListener("resize", handleResize); // add event listener

    return () => window.removeEventListener("resize", handleResize); // cleanup on component unmount
  }, []);

  return (
    <div
      className={`date-picker-container ${
        isFullCalendar
          ? "date-picker-container-full"
          : "date-picker-container-condensed"
      }`}
      data-testid="date-picker"
    >
      {isFullCalendar && (
        <>
          <YearPicker />
          <FormControlLabel
            control={
              <Checkbox
                sx={{
                  color: "black",
                  "&.Mui-checked": {
                    color: "black",
                  },
                }}
              />
            }
            checked={selectedMonth ? true : false}
            onChange={(e, c) => onSelectMonthChange()}
            label="Select Month"
            labelPlacement="start"
          />
          {selectedMonth && <MonthCalendarTable />}
        </>
      )}
      {getIcon()}
    </div>
  );
};
