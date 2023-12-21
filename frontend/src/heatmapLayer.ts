import { Feature } from "geojson";
import type { CircleLayer } from "mapbox-gl";

/**
 * Calculates the number of articles within a given date for a certain location
 */
const getMaxArticleCount = (countKey: string, features: Feature[]): number => {
  const counts: number[] = features.map((feature, index) => {
    if (
      feature.properties !== null &&
      feature.properties[countKey] !== null &&
      feature.properties[countKey] !== undefined
    ) {
      return feature.properties[countKey];
    } else {
      return 0; // Return 0 if the property is null or undefined
    }
  });
  return Math.max(...counts);
};

/**
 * Updates the heatmap points. It scales them according to the number of articles within a location, and colors them based on the average sentiment
 */
export const updateHeatMapLayer = (
  features: Feature[],
  year: number,
  month?: number
): CircleLayer | null => {
  const formattedMonth = month ? month.toString().padStart(2, "0") : "";
  const formattedYear = year.toString();
  const sentimentKey: string =
    (formattedMonth ? `${formattedMonth}-${formattedYear}` : formattedYear) +
    "-sentiment";
  const countKey: string =
    (formattedMonth ? `${formattedMonth}-${formattedYear}` : formattedYear) +
    "-count";

  const maxCount = getMaxArticleCount(countKey, features);

  const MIN_ZOOM_MIN_CIRCLE_SIZE = 1;
  const MIN_ZOOM_MAX_CIRCLE_SIZE = 50;

  const MED_ZOOM_MIN_CIRCLE_SIZE = 5;
  const MED_ZOOM_MAX_CIRCLE_SIZE = 15;

  const circleLayer: CircleLayer = {
    id: "heatmap",
    type: "circle",
    filter: ["!=", ["typeof", ["get", countKey]], "null"], // excludes points that have no articles for that month
    paint: {
      // circle radius based on total number of articles
      "circle-radius": [
        "interpolate",
        ["linear"],
        ["zoom"],
        0,
        [
          "interpolate",
          ["linear"],
          ["get", countKey],
          0,
          MIN_ZOOM_MIN_CIRCLE_SIZE,
          maxCount,
          MIN_ZOOM_MAX_CIRCLE_SIZE,
        ],
        8,
        [
          "interpolate",
          ["linear"],
          ["get", countKey],
          0,
          MED_ZOOM_MIN_CIRCLE_SIZE,
          maxCount,
          MED_ZOOM_MAX_CIRCLE_SIZE,
        ],
      ],
      // color of the circle based on sentiment
      "circle-color": [
        "interpolate",
        ["linear"],
        ["get", sentimentKey],
        0.2,
        "#ff0000", // low sentiment (red)
        0.5,
        "#ffff00", // medium sentiment (yellow)
        0.8,
        "#00ff00", // high sentiment (green)
      ],
      "circle-opacity": 0.75,
    },
  };
  return circleLayer;
};

export const circleLayerDefault: CircleLayer = {
  id: "heatmap",
  type: "circle",
  paint: {
    // Size of the circle
    "circle-radius": [
      "interpolate",
      ["linear"],
      ["zoom"],
      7,
      [
        "interpolate",
        ["exponential", 5],
        ["get", "11-2023-count"],
        1,
        4,
        150,
        12,
      ],
      16,
      [
        "interpolate",
        ["exponential", 5],
        ["get", "11-2023-count"],
        1,
        8,
        150,
        24,
      ],
    ],

    // Color of the circle based on sentiment
    "circle-color": [
      "interpolate",
      ["linear"],
      ["get", "2023-11-sentiment"],
      0,
      "#ff0000", // Low sentiment
      0.5,
      "#ffa500", // Medium sentiment
      1,
      "#ffff00", // High sentiment
    ],

    // Optional: Adjust the circle opacity
    "circle-opacity": 0.75,
  },
};
