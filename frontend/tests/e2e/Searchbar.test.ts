import { test, expect } from "@playwright/test";

/**
 * Test that checks that the search bar component renders and that
 * a user can enter a keyword
 */
test.describe("SearchBar component", () => {
  test.beforeEach(async ({ page }) => {
    await page.goto("http://localhost:5173/");
  });
  test("should be able to see search bar and enter a keyword", async ({
    page,
  }) => {
    await expect(page.getByTestId("searchbar")).toBeVisible();
  });
  test("should be able to enter a keyword and see results", async ({
    page,
  }) => {
    const input = page.getByTestId("searchbar-input");
    await input.click();
    await input.fill("gaza");
    await expect(page.getByTestId("search-results")).toBeVisible();
  });
  test("should be able to click on a date range button to toggle search options", async ({
    page,
  }) => {
    const dateRangeButton = page.locator("#search-date-range-button");
    await expect(dateRangeButton).toBeVisible();
    await dateRangeButton.click();

    const dateRangeCheckbox = page.locator(
      "#search-date-range-option-checkbox"
    );
    await expect(dateRangeCheckbox).toBeVisible();
    await dateRangeCheckbox.click();
  });
});
