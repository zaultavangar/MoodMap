import React from 'react';
import './YearPicker.css';
import NavigateBeforeIcon from '@mui/icons-material/NavigateBefore';
import NavigateNextIcon from '@mui/icons-material/NavigateNext';
import { useRecoilValue } from 'recoil';
import { IconButton } from '@mui/material';
import { selectedYearState } from '~/atoms';
import { useDatePicker } from '~/hooks/useDatePicker';

const MIN_YEAR = 2019; // TODO: subject to change
const MAX_YEAR = new Date().getFullYear();

export const YearPicker = () => {
  // State to track the current date

  const selectedYear = useRecoilValue(selectedYearState);

  const prevYearButtonDisabled: boolean = selectedYear == MIN_YEAR;
  const nextYearButtonDisabled: boolean = selectedYear == MAX_YEAR;

  const { nextYear, prevYear } = useDatePicker();

  return (
    <div className='year-container'>
      <IconButton onClick={prevYear} disabled={prevYearButtonDisabled} className='navigation-button'>
        <NavigateBeforeIcon/>
      </IconButton>
      <div id='current-date'>
        {selectedYear.toString()}
      </div>
      <IconButton onClick={nextYear} disabled={nextYearButtonDisabled} className='navigation-button'>
        <NavigateNextIcon/>
      </IconButton>
    </div>

  );
}
