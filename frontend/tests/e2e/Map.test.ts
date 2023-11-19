import { test, expect } from "@playwright/test";

/**
 * Test that checks that the map component renders
 */
test.describe("Map component", () => {
  test("should be visible", async ({ page }) => {
    await page.goto("http://localhost:5173/");
    await expect(page.getByTestId("map")).toBeVisible();
  });
});
