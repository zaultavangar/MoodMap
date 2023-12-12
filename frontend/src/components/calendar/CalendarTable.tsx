import { useRecoilState } from "recoil";
import { selectedDateRangeState } from "~/atoms";
import './CalendarTable.css';

export const CalendarTable = () => {

  const [selectedDateRange, setSelectedDateRange] = useRecoilState(selectedDateRangeState);
  const MONTHS = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'July', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

  const changeMonth = (monthIdx: number) => {
    let monthStr = monthIdx.toString();
    if (monthIdx<=9) monthStr = "0" + monthStr;
    setSelectedDateRange(monthStr+selectedDateRange.substring(2, 7))
  }

  const renderMonthsTable = () => {
    let tableRows = [];
    for (let row = 0; row < 3; row++) {
      let tableCells = [];
      for (let col = 0; col < 4; col++) {
        const monthIndex = row * 4 + col;
        const month = MONTHS[monthIndex];
        tableCells.push(
          // onClick={() => setSelectedMonth(monthIndex)}
          <td 
            className={`${monthIndex === parseInt(selectedDateRange.substring(0,2))-1 ? 'month-selected': ''}`}
            key={monthIndex} 
            onClick={() => changeMonth(monthIndex+1)}>
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