import './YearPicker.css';
import React from 'react';
import NavigateBeforeIcon from '@mui/icons-material/NavigateBefore';
import NavigateNextIcon from '@mui/icons-material/NavigateNext';
import { useRecoilState } from 'recoil';
import { selectedDateRangeState } from '~/atoms';
import { IconButton } from '@mui/material';

export const YearPicker = () => {
  // State to track the current date

  const MIN_YEAR = 2019; // TODO: subject to change
  const MAX_YEAR = new Date().getFullYear();
  
  const [selectedDateRange, setSelectedDateRange] = useRecoilState(selectedDateRangeState);

  const prevYearButtonDisabled: boolean = parseInt(selectedDateRange.substring(3, 7)) == MIN_YEAR;
  const nextYearButtonDisabled: boolean = parseInt(selectedDateRange.substring(3, 7)) == MAX_YEAR;

  // Function to move to next month
  const nextYear = () => {
    console.log('click')
    const nextYearNum = parseInt(selectedDateRange.substring(3,7)) + 1;
    setSelectedDateRange(selectedDateRange.substring(0,3) + nextYearNum);
  };

  // Function to move to previous month
  const prevYear = (e: React.MouseEvent<HTMLButtonElement, MouseEvent>) => {
    e.preventDefault();
    e.stopPropagation();
    console.error('error')
    console.log('hi')
    
    const prevYearNum = parseInt(selectedDateRange.substring(3,7)) - 1;
    setSelectedDateRange(selectedDateRange.substring(0,3) + prevYearNum);
  };

  // Toggle calendar view


  return (
    <div className='year-container'>
      <IconButton onClick={prevYear} disabled={prevYearButtonDisabled} className='navigation-button'>
        <NavigateBeforeIcon/>
      </IconButton>
      <div id='current-date'>
        {selectedDateRange.substring(3,7)}
      </div>
      <IconButton onClick={nextYear} disabled={nextYearButtonDisabled} className='navigation-button'>
        <NavigateNextIcon  />
      </IconButton>
    </div>

  );
}
