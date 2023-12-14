import React from 'react';
import { useRecoilValue } from "recoil";
import './MonthCalendarTable.css';
import { selectedMonthState } from "~/atoms";
import { useDatePicker } from "~/hooks/useDatePicker";

const MONTHS = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'July', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

export const MonthCalendarTable = () => {

  const selectedMonth = useRecoilValue(selectedMonthState);

  const { onMonthChange } = useDatePicker();

  const renderMonthsTable = () => {
    let tableRows = [];
    for (let row = 0; row < 3; row++) {
      let tableCells = [];
      for (let col = 0; col < 4; col++) {
        const monthIndex = row * 4 + col;
        const month = MONTHS[monthIndex];
        tableCells.push(
          <td 
            className={`${monthIndex === selectedMonth! -1 ? 'month-selected': ''}`}
            key={monthIndex} 
            onClick={() => onMonthChange(monthIndex)}>
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

  return (
    <>
      {renderMonthsTable()}
    </>
  )
}