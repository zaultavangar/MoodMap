import type { FeatureCollection } from "geojson";

export const mockGeojson: FeatureCollection = {
  type: "FeatureCollection",
  features: [
    {
      type: "Feature",
      properties: {
        sentiment: 1,
      },
      geometry: {
        type: "Point",
        coordinates: [-71.4141362441059, 41.82454500035089],
      },
    },
    {
      type: "Feature",
      properties: {
        sentiment: 1,
      },
      geometry: {
        type: "Point",
        coordinates: [34.47, 31.5],
      },
    },
    {
      type: "Feature",
      properties: {
        sentiment: 1,
      },
      geometry: {
        type: "Point",
        coordinates: [35.2736, 31.9464],
      },
    },
    {
      type: "Feature",
      properties: {
        sentiment: 0.3,
      },
      geometry: {
        type: "Point",
        coordinates: [34.7818, 32.0853],
      },
    },
    {
      type: "Feature",
      properties: {
        sentiment: 0.2,
      },
      geometry: {
        type: "Point",
        coordinates: [35.2137, 31.7683],
      },
    },
  ],
};
