import { Feature } from "geojson";
import { describe, it, expect } from "vitest";
import { createFeatureCollection } from "../../src/hooks/useMapManager";

describe("createFeatureCollection", () => {
  it("should correctly generate a feature collection based on a list of features", () => {
    const features: Feature[] = [
      {
        type: "Feature",
        geometry: {
          type: "Point",
          coordinates: [123, 123],
        },
        properties: {},
      },
      {
        type: "Feature",
        geometry: {
          type: "Point",
          coordinates: [321, 321],
        },
        properties: {},
      },
    ];

    const featureCollection = createFeatureCollection(features);

    expect(featureCollection).toStrictEqual({
      type: "FeatureCollection",
      features: features,
    });
  });
});
