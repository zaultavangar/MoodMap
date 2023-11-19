import type { HeatmapLayer } from "mapbox-gl";

const MAX_ZOOM_LEVEL = 9;

export const heatmapLayer: HeatmapLayer = {
  id: "heatmap",
  maxzoom: 9,
  type: "heatmap",
  paint: {
    // Increase the heatmap weight based on frequency and property magnitude
    "heatmap-weight": [
      "interpolate",
      ["linear"],
      ["get", "sentiment"],
      0,
      0,
      1,
      1,
    ],
    // Increase the heatmap color weight weight by zoom level
    // heatmap-intensity is a multiplier on top of heatmap-weight
    "heatmap-intensity": [
      "interpolate",
      ["linear"],
      ["zoom"],
      0,
      1,
      MAX_ZOOM_LEVEL,
      3,
    ],
    // Color ramp for heatmap.  Domain is 0 (low) to 1 (high).
    // Begin color ramp at 0-stop with a 0-transparancy color
    // to create a blur-like effect.
    "heatmap-color": [
      "interpolate",
      ["linear"],
      ["heatmap-density"],
      0,
      "rgba(33,102,172,0)",
      0.2,
      "#af8dc3",
      0.4,
      "#e7d4e8",
      0.6,
      "#d9f0d3",
      0.8,
      "#7fbf7b",
      0.9,
      "#1b7837",
    ],
    // Adjust the heatmap radius by zoom level
    "heatmap-radius": [
      "interpolate",
      ["linear"],
      ["zoom"],
      0,
      2,
      MAX_ZOOM_LEVEL,
      20,
    ],
    // Transition from heatmap to circle layer by zoom level
    "heatmap-opacity": ["interpolate", ["linear"], ["zoom"], 7, 1, 9, 0],
  },
};
