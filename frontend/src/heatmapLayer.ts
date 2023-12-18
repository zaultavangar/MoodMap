import { Feature } from "geojson";
import type { CircleLayer } from "mapbox-gl";

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
  if (maxCount > 10 && maxCount <= 15) return null;
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
          "step",
          ["get", countKey],
          3,
          5,
          10,
          10,
          20,
          15,
          30,
          80,
          20, // default value
        ],
        8,
        [
          "step",
          ["get", countKey],
          5,
          10,
          10,
          12,
          20,
          13,
          30,
          15,
          20, // default value
        ],
      ],
      // color of the circle based on sentiment
      "circle-color": [
        "interpolate",
        ["linear"],
        ["get", sentimentKey],
        0.2,
        "#ff0000", // Low sentiment
        0.7,
        "#00ff00", // High sentiment
      ],
      "circle-opacity": 0.75,
    },
  };
  return circleLayer;
};

export const circleLayerr: CircleLayer = {
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

// export const heatmapLayer: HeatmapLayer = {
//   id: "heatmap",
//   maxzoom: 9,
//   type: "heatmap",
//   paint: {
//     // Increase the heatmap weight based on frequency and property magnitude
//     "heatmap-weight": [
//       "interpolate",
//       ["linear"],
//       ["get", "sentiment"],
//       0,
//       0,
//       1,
//       1,
//     ],
//     // Increase the heatmap color weight weight by zoom level
//     // heatmap-intensity is a multiplier on top of heatmap-weight
//     "heatmap-intensity": [
//       "interpolate",
//       ["linear"],
//       ["zoom"],
//       0,
//       1,
//       MAX_ZOOM_LEVEL,
//       3,
//     ],

//     // Color ramp for heatmap.  Domain is 0 (low) to 1 (high).
//     // Begin color ramp at 0-stop with a 0-transparancy color
//     // to create a blur-like effect.
//     "heatmap-color": [
//       "interpolate",
//       ["linear"],
//       ["heatmap-density"],
//       0,
//       "rgba(33,102,172,0)",
//       0.2,
//       "#af8dc3",
//       0.4,
//       "#e7d4e8",
//       0.6,
//       "#d9f0d3",
//       0.8,
//       "#7fbf7b",
//       0.9,
//       "#1b7837",
//     ],
//     // Adjust the heatmap radius by zoom level
//     "heatmap-radius": [
//       "interpolate",
//       ["linear"],
//       ["zoom"],
//       0,
//       2,
//       MAX_ZOOM_LEVEL,
//       20,
//     ],
//     // Transition from heatmap to circle layer by zoom level
//     "heatmap-opacity": ["interpolate", ["linear"], ["zoom"], 7, 1, 9, 0],
//   },
// };
