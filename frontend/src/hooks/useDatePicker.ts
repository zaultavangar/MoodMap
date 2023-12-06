import { useState } from "react";

export function useDatePicker() {
  const [selectedDateRange, setSelectedDateRange] = useState<string>('11-2023');
  return {
    selectedDateRange,
    setSelectedDateRange
  }
}