import React from "react";
import { useRecoilState } from "recoil";
import {
  isFullCalendarState,
  selectedMonthState,
  selectedYearState,
} from "~/atoms";
import UnfoldLessIcon from "@mui/icons-material/UnfoldLess";
import UnfoldMoreIcon from "@mui/icons-material/UnfoldMore";

export function useDatePicker() {
  const [isFullCalendar, setIsFullCalendar] =
    useRecoilState(isFullCalendarState);
  const [selectedMonth, setSelectedMonth] = useRecoilState(selectedMonthState);
  const [selectedYear, setSelectedYear] = useRecoilState(selectedYearState);

  const handleResize = () =>
    setIsFullCalendar(needsResizing(window.innerWidth));

  const toggleCalendarView = () => {
    setIsFullCalendar(!isFullCalendar);
  };

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

  const onSelectMonthChange = () => {
    setSelectedMonth(changeSelectedMonth(selectedMonth));
  };

  const onMonthChange = (monthIndex: number) => {
    setSelectedMonth(changeMonth(monthIndex));
  };

  const nextYear = () => {
    setSelectedYear(changeToNextYear(selectedYear));
  };

  // Function to move to previous month
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
