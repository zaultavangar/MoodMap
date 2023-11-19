import { test, expect } from "@playwright/test";

/**
 * Test that checks that the search bar component renders and that
 * a user can enter a keyword
 */
test.describe("SearchBar component", () => {
  test("should be able to see search bar and enter a keyword", async ({
    page,
  }) => {
    await page.goto("http://localhost:5173/");
    await expect(page.getByTestId("searchbar")).toBeVisible();
    await page.getByPlaceholder("Search an area by keyword").click();
    await page.getByPlaceholder("Search an area by keyword").fill("Providence");
  });
});
