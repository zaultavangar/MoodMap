import React, { useEffect } from 'react'
import './DatePicker.css'
import { YearPicker } from '../calendar/YearPicker';
import UnfoldLessIcon from '@mui/icons-material/UnfoldLess';
import UnfoldMoreIcon from '@mui/icons-material/UnfoldMore';
import { useRecoilState, useRecoilValue } from 'recoil';
import { isFullCalendarState, selectedDateRangeState } from '~/atoms';
import { getSliderMarksMap, getSliderValueFromDateString } from './DatePickerUtils';
import { CalendarTable } from '../calendar/CalendarTable';
import { Slider } from '@mui/material';

export const DatePicker = () => {
  // control date from here or from a useDatePicker
  // should be on the right hand side of the page 

  // to toggle between full calendar and collapsed view
  const [isFullCalendar, setIsFullCalendar] = useRecoilState(isFullCalendarState);

  const [selectedDateRange, setSelectedDateRange] = useRecoilState(selectedDateRangeState);

  const toggleCalendarView = () => {
    setIsFullCalendar(!isFullCalendar);
  };


  const handleResize = () => {
    if (window.innerWidth <= 760) {
      setIsFullCalendar(false);
    } else {
      setIsFullCalendar(true);
    }
  };


  const sliderMarks = getSliderMarksMap(parseInt(selectedDateRange.substring(3, 7)));

  useEffect(() => {
    handleResize();
    window.addEventListener('resize', handleResize); // add event listener

    return () => window.removeEventListener('resize', handleResize); // cleanup on component unmount
  }, []);

  const onSliderChange = (value: number | number[]) => {
    if (typeof value === "number"){
      const monthIndex: number = Math.abs(value/9 -11); // 0 dec, 12 jan
      const monthStr = monthIndex >= 9 ? (monthIndex+1).toString() : `0${(monthIndex+1).toString()}`;
      setSelectedDateRange(`${monthStr}-${selectedDateRange.substring(3,7)}`);
    }
   

  }

  return (
    <div className={`date-picker-container ${isFullCalendar ? 'date-picker-container-full': 'date-picker-container-slider'}`}>
        <YearPicker/>
      {isFullCalendar ? 
        <CalendarTable/> 
      :
      <div className='slider-container'>
        <Slider
          color='info'
          size='small'
          orientation='vertical'
          value={getSliderValueFromDateString(selectedDateRange)}
          defaultValue={0}
          marks={sliderMarks}
          step={null}
          onChange={(e, value, activeThumb) => onSliderChange(value)}
          />
      </div>
    }
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
     
      
    </div>
  )
}
