import { Feature, FeatureCollection, Point } from "geojson";
import { useRecoilState, useRecoilValue } from "recoil";
import {
  isExpandedOverviewPanelState,
  mapFeatureCollectionState,
  selectedMonthState,
  selectedYearState,
} from "~/atoms";
import { LocationStats, LocationToDetailsMap, StatsOverviewMap } from "~/types";

/**
 * useOverviewPanel is a custom React hook that contains the logic for the OverviewPanel component.  Separating the logic from the component allows to more easily make changes to the view and to test the logic.
 */
export function useOverviewPanel() {
  const featureCollection = useRecoilValue(mapFeatureCollectionState);

  const selectedYear = useRecoilValue(selectedYearState);
  const selectedMonth = useRecoilValue(selectedMonthState);
  const [isExpandedOverviewPanel, setIsExpandedOverviewPanel] = useRecoilState(
    isExpandedOverviewPanelState
  );

  /**
   * Need to get the keys for the number of articles (count) and the sentiment because a given location may have articles for different months and years
   */
  const sentimentKey: string = getSentimentKey(selectedMonth, selectedYear);
  const countKey: string = getCountKey(selectedMonth, selectedYear);

  const toggleOverviewPanelDisplay = () => {
    setIsExpandedOverviewPanel(!isExpandedOverviewPanel);
  };

  return {
    getOverviewPanelMap: () =>
      computeOverviewPanelMap(featureCollection, countKey, sentimentKey),
    // getOverviewPanelMap,
    getDateStr: () => getDateStr(selectedMonth, selectedYear),
    isExpandedOverviewPanel,
    setIsExpandedOverviewPanel,
    toggleOverviewPanelDisplay,
  };
}

/**
 * Pure functions were abstracted away from the hook so that functionality could be more easily tested in isolation and reduce the dependence on React's state management
 */

export function computeOverviewPanelMap(
  featureCollection: FeatureCollection,
  countKey: string,
  sentimentKey: string
): StatsOverviewMap {
  const features = featureCollection.features;

  // Gets the number of articles, average sentiment, and coordinates for each location
  const statsOverviewMap: { [key: string]: LocationStats }[] = features.reduce(
    (acc: LocationToDetailsMap[], feature) => {
      const properties = feature.properties;
      const geometry = feature.geometry;
      if (
        properties &&
        properties.location &&
        properties[countKey] &&
        properties[sentimentKey] &&
        isValidGeometryPoint(geometry)
      ) {
        const locationDetails: LocationStats = {
          count: properties![countKey] as number,
          sentiment: properties![sentimentKey] as number,
          coordinates: geometry.coordinates,
        };
        acc.push({ [properties.location]: locationDetails });
      }
      return acc;
    },
    []
  );

  // Finds the most mentioned locations
  const topFiveMostMentioned = statsOverviewMap
    .sort((locationA, locationB) => {
      const locationA_count = Object.values(locationA)[0].count;
      const locationB_count = Object.values(locationB)[0].count;

      return locationB_count - locationA_count;
    })
    .slice(0, 5);

  const sortedBySentiment = statsOverviewMap.sort((locationA, locationB) => {
    const locationA_sentiment = Object.values(locationA)[0].sentiment;
    const locationB_sentiment = Object.values(locationB)[0].sentiment;

    return locationB_sentiment - locationA_sentiment;
  }); // sorted in descending order

  // Finds the most positively viewed locations
  const topFivePositiveSentiment = sortedBySentiment.slice(0, 5);
  // Finds the most negatively viewed locations
  const topFiveNegativeSentiment = sortedBySentiment.slice(
    sortedBySentiment.length - 5,
    sortedBySentiment.length
  );

  return {
    mostMentioned: topFiveMostMentioned,
    mostPositive: topFivePositiveSentiment,
    mostNegative: topFiveNegativeSentiment,
  };
}

export function getDateStr(
  selectedMonth: number,
  selectedYear: number
): string {
  if (selectedMonth !== undefined) {
    const date = new Date(selectedYear, selectedMonth - 1);
    return date.toLocaleString("default", {
      month: "short",
      year: "numeric",
    });
  } else {
    return selectedYear.toString();
  }
}

export function isValidGeometryPoint(geometry: any): geometry is Point {
  return geometry ?? geometry.coordinates;
}

export function getCountKey(
  selectedMonth: number,
  selectedYear: number
): string {
  return (
    (selectedMonth
      ? `${selectedMonth
          .toString()
          .padStart(2, "0")}-${selectedYear.toString()}`
      : `${selectedYear.toString()}`) + "-count"
  );
}

export function getSentimentKey(
  selectedMonth: number,
  selectedYear: number
): string {
  return (
    (selectedMonth
      ? `${selectedMonth
          .toString()
          .padStart(2, "0")}-${selectedYear.toString()}`
      : `${selectedYear.toString()}`) + "-sentiment"
  );
}
