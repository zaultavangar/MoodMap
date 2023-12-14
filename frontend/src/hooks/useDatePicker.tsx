import React from 'react';
import { useRecoilState } from "recoil"
import { isFullCalendarState, selectedMonthState, selectedYearState } from "~/atoms"
import UnfoldLessIcon from '@mui/icons-material/UnfoldLess';
import UnfoldMoreIcon from '@mui/icons-material/UnfoldMore';

export function useDatePicker(){
  const [isFullCalendar, setIsFullCalendar] = useRecoilState(isFullCalendarState);
  const [selectedMonth, setSelectedMonth] = useRecoilState(selectedMonthState);
  const [selectedYear, setSelectedYear] = useRecoilState(selectedYearState);

  const handleResize = () => {
    if (window.innerWidth <= 760) {
      setIsFullCalendar(false);
    } else {
      setIsFullCalendar(true);
    }
  };

  const toggleCalendarView = () => {
    setIsFullCalendar(!isFullCalendar);
  };

  const getIcon = (): JSX.Element => {
    return (
      <div 
        className='toggle-container'
        style={{display: 'flex', justifyContent: 'flex-end', alignItems: 'center'}}
        >
        <button 
          className = "toggle-icon-button" 
          onClick={toggleCalendarView}
          >
          {isFullCalendar ? 
            <UnfoldLessIcon className='toggle-icon'/> : 
            <UnfoldMoreIcon className='toggle-icon'/>
          }
        </button>
      </div>
    )
  }

  const onSelectMonthChange = () => {
    setSelectedMonth(selectedMonth ? undefined : new Date().getMonth()+1);
  }

  const onMonthChange = (monthIndex: number) => {
    setSelectedMonth(monthIndex+1);
  }

  const nextYear = () => {
    setSelectedYear(selectedYear+1);
  };

  // Function to move to previous month
  const prevYear = () => {    
    setSelectedYear(selectedYear-1);
  };

  return {
    handleResize,
    getIcon,
    onSelectMonthChange,
    onMonthChange,
    nextYear,
    prevYear
  }
}
