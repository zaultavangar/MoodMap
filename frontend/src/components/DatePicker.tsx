import React from 'react'
import '../DatePicker.css'
import ScheduleIcon from "@mui/icons-material/Schedule";
import Calendar from 'react-calendar';
import { IconButton } from '@mui/material';

export const DatePicker = () => {
  // control date from here or from a useDatePicker
  // should be on the right hand side of the page 
  return (
    <div className='date-picker-container'>
      <IconButton type="button" aria-label="filter by month/year" style={{color: 'white'}}>
      <ScheduleIcon />
    </IconButton>
      <Calendar
        className="date-picker-calendar"
        activeStartDate={new Date()}
        defaultView='month'
        maxDate={new Date()}
        minDate={new Date(2022, 0, 1)}
        />
        {/* Should also include a button that can collapse this and just turn it into a slider 
        so that the user can see changes better  */}
    </div>
  )
}
