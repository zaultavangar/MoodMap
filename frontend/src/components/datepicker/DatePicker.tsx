import React, { useState } from 'react'
import './DatePicker.css'
import { Calendar } from '../calendar/Calendar';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import ExpandLessIcon from '@mui/icons-material/ExpandLess';
import { Slider } from '@mui/material';

export const DatePicker = () => {
  // control date from here or from a useDatePicker
  // should be on the right hand side of the page 

  const [selectedDateRange, setSelectedDateRange] = useState<string>('11-2023')

  // to toggle between full calendar and collapsed view
  const [isFullCalendar, setIsFullCalendar] = useState(true);

  const toggleCalendarView = () => {
    setIsFullCalendar(!isFullCalendar);
  };

  function valuetext(value: number) {
    return `${value}°C`;
  }

  return (
    <div className={`date-picker-container ${isFullCalendar ? 'date-picker-container-full': 'date-picker-container-slider'}`}>
      {isFullCalendar ? 
      <Calendar 
      selectedDateRange={selectedDateRange}
      setSelectedDateRange={setSelectedDateRange}
    /> :
      <div className='slider-container'>
        <Slider 
          orientation='vertical' 
          defaultValue={30}
          aria-label="Always visible"
          getAriaValueText={valuetext}
          step={10}
          min={2022}
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
          <ExpandLessIcon className='toggle-icon'/> : 
          <ExpandMoreIcon className='toggle-icon'/>
          }
      </button>
    </div>
     
      
    </div>
    // <div className='date-picker-container'>
    //   {/* <IconButton type="button" aria-label="filter by month/year" style={{color: 'white'}}>
    //   <ScheduleIcon />
    // </IconButton> */}
    // 

    //     {/* Should also include a button that can collapse this and just turn it into a slider 
    //     so that the user can see changes better  */}
    // </div>
  )
}
