import React from "react";
import { useRecoilState } from "recoil";
import {
  isFullCalendarState,
  selectedMonthState,
  selectedYearState,
} from "~/atoms";
import UnfoldLessIcon from "@mui/icons-material/UnfoldLess";
import UnfoldMoreIcon from "@mui/icons-material/UnfoldMore";

/**
 * useDatePicker is a custom React hook that contains the logic for the DatePicker component. Separating the logic from the component allows to more easily make changes to the view and to test the logic.
 */
export function useDatePicker() {
  const [isFullCalendar, setIsFullCalendar] =
    useRecoilState(isFullCalendarState);
  const [selectedMonth, setSelectedMonth] = useRecoilState(selectedMonthState);
  const [selectedYear, setSelectedYear] = useRecoilState(selectedYearState);

  /**
   * Resizes the DatePicker depending on the screen size
   */
  const handleResize = () =>
    setIsFullCalendar(needsResizing(window.innerWidth));

  const toggleCalendarView = () => {
    setIsFullCalendar(!isFullCalendar);
  };

  /**
   * Changes the icon to be displayed depending on whether the DatePicker is expanded or not
   */
  const getIcon = (): JSX.Element => {
    return (
      <div
        className="toggle-container"
        style={{
          display: "flex",
          justifyContent: "flex-end",
          alignItems: "center",
        }}
      >
        <button className="toggle-icon-button" onClick={toggleCalendarView}>
          {isFullCalendar ? (
            <UnfoldLessIcon className="toggle-icon" />
          ) : (
            <UnfoldMoreIcon className="toggle-icon" />
          )}
        </button>
      </div>
    );
  };

  // Changes the selected month and deals with the initial state
  const onSelectMonthChange = () => {
    setSelectedMonth(changeSelectedMonth(selectedMonth));
  };

  // Changes the month based on the month's index
  const onMonthChange = (monthIndex: number) => {
    setSelectedMonth(changeMonth(monthIndex));
  };

  // Function to move to the next month
  const nextYear = () => {
    setSelectedYear(changeToNextYear(selectedYear));
  };

  // Function to move to the previous month
  const prevYear = () => {
    setSelectedYear(changeToPrevYear(selectedYear));
  };

  return {
    handleResize,
    getIcon,
    onSelectMonthChange,
    onMonthChange,
    nextYear,
    prevYear,
  };
}

/**
 * Pure functions were abstracted away from the hook so that functionality could be more easily tested in isolation and reduce the dependence on React's state management
 */

export function changeSelectedMonth(selectedMonth: number): number | undefined {
  return selectedMonth ? undefined : new Date().getMonth() + 1;
}

export function needsResizing(width: number) {
  return width <= 760 ? false : true;
}

export function changeMonth(monthIndex: number) {
  return monthIndex + 1;
}

export function changeToNextYear(year: number) {
  return year + 1;
}

export function changeToPrevYear(year: number) {
  return year - 1;
}
