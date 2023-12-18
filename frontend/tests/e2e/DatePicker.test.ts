import { test, expect } from "@playwright/test";

test.describe("DatePicker component", () => {
  test.beforeEach(async ({ page }) => {
    await page.goto("http://localhost:5173/");
  });
  test("should be visible with table of months", async ({ page }) => {
    await expect(page.getByTestId("date-picker")).toBeVisible();
    await expect(page.getByTestId("month-calendar-table")).toBeVisible();
  });
  test("should be able to go to the previous and next year", async ({
    page,
  }) => {
    const prevButton = page.getByTestId("prev-year-button");
    const nextButton = page.getByTestId("next-year-button");
    const currentYear = page.getByTestId("current-year");
    await prevButton.click();
    await expect(currentYear).toHaveText("2022");
    await nextButton.click();
    await expect(currentYear).toHaveText("2023");
  });
  test("should be able to click on a month", async ({ page }) => {
    const button = page.getByTestId("month-calendar-table").getByText("Nov");
    await button.click();
  });
});
