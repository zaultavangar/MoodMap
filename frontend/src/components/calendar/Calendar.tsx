import './Calendar.css';
import React, { SetStateAction, useCallback, useEffect, useState, } from 'react';
import { IconButton} from '@mui/material';
import NavigateBeforeIcon from '@mui/icons-material/NavigateBefore';
import NavigateNextIcon from '@mui/icons-material/NavigateNext';

interface CalendarProps {
  selectedDateRange: string,
  setSelectedDateRange: React.Dispatch<SetStateAction<string>>;
}

export const Calendar = (props: CalendarProps) => {
  // State to track the current date

  const MONTHS = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'July', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
  const MIN_YEAR = 2022; // TODO: subject to change
  const MAX_YEAR = new Date().getFullYear();
  
  const {selectedDateRange, setSelectedDateRange} = props;

  const [selectedMonth, setSelectedMonth] = useState<string>(selectedDateRange.substring(0,2));
  const [selectedYear, setSelectedYear]  = useState<string>(selectedDateRange.substring(3, 7));

  const prevYearButtonDisabled: boolean = parseInt(selectedDateRange.substring(3, 7)) == MIN_YEAR;
  const nextYearButtonDisabled: boolean = parseInt(selectedDateRange.substring(3, 7)) == MAX_YEAR;

  // Function to move to next month
  const nextYear = () => {
    console.log('click')
    const nextYearNum = parseInt(selectedDateRange.substring(3,7)) + 1;
    setSelectedDateRange(selectedDateRange.substring(0,3) + nextYearNum);
  };

  // Function to move to previous month
  const prevYear = () => {
    console.error('click')
    
    const prevYearNum = parseInt(selectedDateRange.substring(3,7)) - 1;
    setSelectedDateRange(selectedDateRange.substring(0,3) + prevYearNum);
  };

  // Toggle calendar view


  const renderMonthsTable = () => {
    let tableRows = [];
    for (let row = 0; row < 3; row++) {
      let tableCells = [];
      for (let col = 0; col < 4; col++) {
        const monthIndex = row * 4 + col;
        const month = MONTHS[monthIndex];
        console.error(parseInt(selectedDateRange.substring(0,2)));
        tableCells.push(
          // onClick={() => setSelectedMonth(monthIndex)}
          <td 
            className={`${monthIndex === parseInt(selectedDateRange.substring(0,2)) ? 'month-selected': ''}`}
            key={monthIndex} 
            onClick={() => changeMonth(monthIndex)}>
            {month}
          </td>
        );
      }
      tableRows.push(<tr key={row}>{tableCells}</tr>);
    }
    return (
      <table className='calendar-table'>
        <tbody>{tableRows}</tbody>
      </table>
    )
  }

  const changeMonth = (monthIdx: number) => {
    let monthStr = monthIdx.toString();
    if (monthIdx<=9) monthStr = "0" + monthStr;
    setSelectedDateRange(monthStr+selectedDateRange.substring(2, 7))
  }

    

  return (
    <div className='calendar-container'>
      <div className='year-container'>
        <button className='navigation-button' onClick={() => prevYear} disabled={prevYearButtonDisabled}>
            <NavigateBeforeIcon />
        </button>
        <div id='current-date'>
          {selectedDateRange.substring(3,7)}
        </div>
        <button className='navigation-button'  onClick={nextYear} disabled={nextYearButtonDisabled}>
          <NavigateNextIcon  />
        </button>

      </div>
      {renderMonthsTable()}
    </div>
  );
}
