import { describe, it, expect } from "vitest";
import {
  computeOverviewPanelMap,
  getCountKey,
  getDateStr,
  getSentimentKey,
  isValidGeometryPoint,
} from "../../src/hooks/useOverviewPanel";

describe("getCountKey", () => {
  it("should return `<selected_year>-count` if the selected month is null", () => {
    const selectedMonth = null;
    const selectedYear = 2023;

    const result = getCountKey(selectedMonth, selectedYear);
    expect(result).toEqual("2023-count");
  });
  it("should return `<selected_month>-<selected_year>-count` if the selected month is not null", () => {
    const selectedMonth = 12;
    const selectedYear = 2023;

    const result = getCountKey(selectedMonth, selectedYear);
    expect(result).toEqual("12-2023-count");
  });
});

describe("getSentimentKey", () => {
  it("should return `<selected_year>-sentiment` if the selected month is null", () => {
    const selectedMonth = null;
    const selectedYear = 2023;

    const result = getSentimentKey(selectedMonth, selectedYear);
    expect(result).toEqual("2023-sentiment");
  });
  it("should return `<selected_month>-<selected_year>-sentiment` if the selected month is not null", () => {
    const selectedMonth = 12;
    const selectedYear = 2023;

    const result = getSentimentKey(selectedMonth, selectedYear);
    expect(result).toEqual("12-2023-sentiment");
  });
});

describe("getDateStr", () => {
  it("should return the year if the selected month is undefined", () => {
    const selectedMonth = undefined;
    const selectedYear = 2023;

    expect(getDateStr(selectedMonth, selectedYear)).toBe("2023");
  });
  it("should return the month (in short-form) and year if the selected month is defined", () => {
    const selectedMonth = 11;
    const selectedYear = 2023;

    expect(getDateStr(selectedMonth, selectedYear)).toBe("Nov 2023");
  });
});

describe("computerOverviewPanelMap", () => {
  it("should filter features into most mentioned, most positive, and most negative based on a selected month and year", () => {
    const featureCollection = {
      type: "FeatureCollection",
      features: [
        {
          type: "Feature",
          properties: {
            location: "Berlin",
            "11-2023-count": 5,
            "11-2023-sentiment": 0.5,
          },
          geometry: {
            type: "Point",
            coordinates: [123, 123],
          },
        },
        {
          type: "Feature",
          properties: {
            location: "Tokyo",
            "11-2023-count": 10,
            "11-2023-sentiment": 0.2,
          },
          geometry: {
            type: "Point",
            coordinates: [123, 123],
          },
        },
        {
          type: "Feature",
          properties: {
            location: "Madrid",
            "11-2023-count": 1,
            "11-2023-sentiment": 0.6,
          },
          geometry: {
            type: "Point",
            coordinates: [123, 123],
          },
        },
      ],
    };

    const countKey = "11-2023-count";
    const sentimentKey = "11-2023-sentiment";

    const result = computeOverviewPanelMap(
      featureCollection,
      countKey,
      sentimentKey
    );
    expect(result).toStrictEqual({
      mostMentioned: [
        {
          Tokyo: {
            count: 10,
            sentiment: 0.2,
            coordinates: [123, 123],
          },
        },
        {
          Berlin: {
            count: 5,
            sentiment: 0.5,
            coordinates: [123, 123],
          },
        },
        {
          Madrid: {
            count: 1,
            sentiment: 0.6,
            coordinates: [123, 123],
          },
        },
      ],
      mostPositive: [
        {
          Madrid: {
            count: 1,
            sentiment: 0.6,
            coordinates: [123, 123],
          },
        },
        {
          Berlin: {
            count: 5,
            sentiment: 0.5,
            coordinates: [123, 123],
          },
        },
        {
          Tokyo: {
            count: 10,
            sentiment: 0.2,
            coordinates: [123, 123],
          },
        },
      ],
      mostNegative: [
        {
          Berlin: {
            count: 5,
            sentiment: 0.5,
            coordinates: [123, 123],
          },
        },
        {
          Tokyo: {
            count: 10,
            sentiment: 0.2,
            coordinates: [123, 123],
          },
        },
      ],
    });
  });
});
