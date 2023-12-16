import { Point } from "geojson";
import { useRecoilState, useRecoilValue } from "recoil"
import { isExpandedOverviewPanelState, mapFeatureCollectionState, selectedMonthState, selectedYearState } from "~/atoms"
import { LocationStats, LocationToDetailsMap, StatsOverviewMap } from "~/types";

export function useOverviewPanel() {

  const featureCollection = useRecoilValue(mapFeatureCollectionState);

  const selectedYear = useRecoilValue(selectedYearState);
  const selectedMonth = useRecoilValue(selectedMonthState);
  const [isExpandedOverviewPanel, setIsExpandedOverviewPanel] = useRecoilState(
    isExpandedOverviewPanelState
  );

  const sentimentKey: string = (selectedMonth ? `${selectedMonth.toString()}-${selectedYear.toString()}` : `${selectedYear.toString()}`) + '-sentiment';
  const countKey: string = (selectedMonth ? `${selectedMonth.toString()}-${selectedYear.toString()}` : `${selectedYear.toString()}`) + '-count';

  const isValidGeometryPoint = (geometry: any): geometry is Point => {
    return geometry ?? geometry.coordinates;
  }

  const getDateStr = () => {
    if (selectedMonth !== undefined) {
      const date = new Date(selectedYear, selectedMonth - 1);
      return date.toLocaleString("default", { month: "short", year: "numeric" });
    } else {
      return selectedYear.toString();
    }
  };

  const toggleOverviewPanelDisplay = () => {
    setIsExpandedOverviewPanel(!isExpandedOverviewPanel);
  }

  const getOverviewPanelMap = (): StatsOverviewMap => {
    const features = featureCollection.features;


    const statsOverviewMap: {[key: string]: LocationStats}[] = features.reduce((acc: LocationToDetailsMap[], feature) => {
      const properties = feature.properties;
      const geometry = feature.geometry;
      if (properties && properties.location && properties[countKey] && properties[sentimentKey] && isValidGeometryPoint(geometry)){
        const locationDetails: LocationStats = {
          count: properties![countKey] as number,
          sentiment: properties![sentimentKey] as number,
          coordinates: geometry.coordinates 
        };
        acc.push({[properties.location] : locationDetails})
      }
      return acc;
    }, [])

    const topFiveMostMentioned = statsOverviewMap.sort((locationA, locationB) => {
      const locationA_count = Object.values(locationA)[0].count;
      const locationB_count = Object.values(locationB)[0].count;

      return locationB_count - locationA_count;
    }).slice(0,5);

    const sortedBySentiment = statsOverviewMap.sort((locationA, locationB) => {
      const locationA_sentiment = Object.values(locationA)[0].sentiment;
      const locationB_sentiment = Object.values(locationB)[0].sentiment;

      return locationB_sentiment - locationA_sentiment;
    }); // sorted in descending order


    const topFivePositiveSentiment = sortedBySentiment.slice(0,5);
    const topFiveNegativeSentiment = sortedBySentiment.slice(sortedBySentiment.length-5, sortedBySentiment.length);

    return {
      mostMentioned: topFiveMostMentioned,
      mostPositive: topFivePositiveSentiment,
      mostNegative: topFiveNegativeSentiment
    }

  }

  return {
    getOverviewPanelMap,
    getDateStr,
    isExpandedOverviewPanel,
    setIsExpandedOverviewPanel,
    toggleOverviewPanelDisplay
  }
}