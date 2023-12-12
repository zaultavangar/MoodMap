import { StatSyncFn } from "fs";
import { Geometry, Point } from "geojson";
import { useRecoilValue } from "recoil"
import { mapFeatureCollectionState, selectedDateRangeState } from "~/atoms"

export type StatsOverviewMap = {
  mostMentioned: LocationToDetailsMap[]
  mostPositive: LocationToDetailsMap[]
  mostNegative: LocationToDetailsMap[]
};

export type LocationToDetailsMap = {
  [key: string]: LocationStats;
}

export type LocationStats = {
  count: number
  sentiment: number
  coordinates: number[]
};

export function useStatsOverview() {

  const featureCollection = useRecoilValue(mapFeatureCollectionState);
  const selectedDateRange = useRecoilValue(selectedDateRangeState);
  const sentimentKey = selectedDateRange+'-sentiment';
  const countKey = selectedDateRange+'-count'

  const isValidGeometryPoint = (geometry: any): geometry is Point => {
    return geometry ?? geometry.coordinates;
  }

  const getStatsOverviewMap = (): StatsOverviewMap => {
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
    getStatsOverviewMap
  }
}