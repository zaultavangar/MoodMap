import {type Mark} from '@mui/base/useSlider/useSlider.types'

export const getSliderMarksMap = (year: number): Mark[] => {
  const sliderMarks: Mark[] = [];
  const endMonth = 11; // December is 11

  let currentMonth = endMonth;
  let index = 0;

  while (currentMonth >= 0){
    const monthName = new Date(year, currentMonth).toLocaleString('default', {month: 'short'});
    const newMark: Mark = {
      value: index*9, // needs to be 9 for proper display
      label: `${monthName}`
    }
    sliderMarks.push(newMark)

    currentMonth--;
    index++;

  }

  return sliderMarks;

}

export const getSliderValueFromDateString = (date: string) => {
  const monthStr = date.substring(0,2); // 12
  console.error(monthStr);
  const reversedMonth = Math.abs(parseInt(monthStr)-12); 
  return reversedMonth*9; // times 9 for a valid slider position
}