import { describe, it, expect } from "vitest";
import {
  changeMonth,
  changeSelectedMonth,
  changeToNextYear,
  changeToPrevYear,
  needsResizing,
} from "~hooks/useDatePicker";

describe("needsResizing", () => {
  it("should return false if window width is less than the breakpoint", () => {
    expect(needsResizing(700)).toBe(false);
  });
  it("should return false if window width is equal the breakpoint", () => {
    expect(needsResizing(760)).toBe(false);
  });
  it("should return true if window width is greater than the breakpoint", () => {
    expect(needsResizing(800)).toBe(true);
  });
});

describe("changeSelectedMonth", () => {
  it("should return undefined if selected month is defined", () => {
    expect(changeSelectedMonth(1)).toBe(undefined);
  });
  it("should return the current month if the selected month is undefined", () => {
    expect(changeSelectedMonth(undefined)).toBe(12);
  });
});

describe("changeMonth", () => {
  for (let index = 0; index < 12; index++) {
    it("should return the currect month number given an month's index", () => {
      expect(changeMonth(index)).toBe(index + 1);
    });
  }
});

describe("changeToNextYear", () => {
  it("should correctly return the next year", () => {
    expect(changeToNextYear(2022)).toBe(2023);
  });
});

describe("changeToPrevYear", () => {
  it("should correctly return the prev year", () => {
    expect(changeToPrevYear(2022)).toBe(2021);
  });
});
