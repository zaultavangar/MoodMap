import type { FeatureCollection } from "geojson";

export const mockGeojson: FeatureCollection = {
  type: "FeatureCollection",
  features: [
    {
      type: "Feature",
      properties: {
        region: "Gaza",
        articles: [
          {
            title:
              "Dialogue and Diplomacy: Key Focus in Addressing Gaza Situation",
            description:
              "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
          },
          {
            title:
              "Dialogue and Diplomacy: Key Focus in Addressing Gaza Situation",
            description:
              "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
          },
        ],
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
        region: "West Bank",
        articles: [
          {
            title:
              "Dialogue and Diplomacy: Key Focus in Addressing Gaza Situation",
            description:
              "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
          },
          {
            title:
              "Dialogue and Diplomacy: Key Focus in Addressing Gaza Situation",
            description:
              "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
          },
        ],
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
        region: "Tel Aviv",
        articles: [
          {
            title:
              "Dialogue and Diplomacy: Key Focus in Addressing Gaza Situation",
            description:
              "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
          },
          {
            title:
              "Dialogue and Diplomacy: Key Focus in Addressing Gaza Situation",
            description:
              "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
          },
        ],
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
        region: "Jerusalem",
        articles: [
          {
            title:
              "Dialogue and Diplomacy: Key Focus in Addressing Gaza Situation",
            description:
              "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
          },
          {
            title:
              "Dialogue and Diplomacy: Key Focus in Addressing Gaza Situation",
            description:
              "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
          },
        ],
        sentiment: 0.2,
      },
      geometry: {
        type: "Point",
        coordinates: [35.2137, 31.7683],
      },
    },
  ],
};
