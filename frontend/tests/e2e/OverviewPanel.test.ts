import { test, expect } from "@playwright/test";

test.describe("OverviewPanel component", () => {
  test.beforeEach(async ({ page }) => {
    await page.goto("http://localhost:5173/");
  });
  test("should be visible with list of categories", async ({ page }) => {
    await expect(page.getByTestId("overview-panel")).toBeVisible();
    await expect(page.getByTestId("overview-categories")).toBeVisible();
  });
  test("should be able to minimize and then maximize panel", async ({
    page,
  }) => {
    const minButton = page.getByTestId("overview-minimize-button");
    const maxButton = page.getByTestId("overview-maximize-button");
    await minButton.click();
    await maxButton.click();
  });
});
