import { act, renderHook } from "@testing-library/react-hooks";
import { describe, expect, it } from "vitest";
import {
  ArticleSummary,
  HeatmapInfo,
  useHeatmapPopup,
} from "../../src/hooks/useHeatmapPopup";

describe("useHeatmapPopup", () => {
  it("should open and close the heatmap popup", () => {
    const { result } = renderHook(() => useHeatmapPopup());

    // Initial state should be null
    expect(result.current.heatmapInfo).toBeNull();

    const mockArticles: ArticleSummary[] = [
      { title: "Test Title", description: "Test Description" },
    ];

    // Open the popup and check if the state is updated
    const mockHeatmapInfo: HeatmapInfo = {
      region: "Test Region",
      articles: mockArticles,
      longitude: 0,
      latitude: 0,
      sentiment: 0,
    };

    act(() => result.current.handlePopupOpen(mockHeatmapInfo));

    expect(result.current.heatmapInfo).toEqual(mockHeatmapInfo);

    // Close the popup and check if the state is reset to null
    act(() => result.current.handlePopupClose());
    expect(result.current.heatmapInfo).toBeNull();
  });
});
