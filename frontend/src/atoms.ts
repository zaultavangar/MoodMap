import { RecoilState, atom } from "recoil";
import { LocationPopupInfo } from "./hooks/useLocationPopup";
import { CircleLayer } from "mapbox-gl";
import { circleLayerr } from "./components/heatmapLayer";
import { Feature, FeatureCollection, GeoJsonProperties, Geometry } from "geojson";

export const searchQueryState: RecoilState<string> = atom({
  key: 'searchQuery',
  default: ''
})

export const searchResultsState: RecoilState<string[]> = atom({
  key: 'searchResults',
  default: [] as string[]
})

export const searchDateFilterPopoverAnchorElState: RecoilState<HTMLButtonElement | null> = atom({
  key: 'searchDateFilterPopoverAnchorEl',
  default: null as HTMLButtonElement | null
})

export const selectedDateRangeState: RecoilState<string> = atom({
  key: 'selectedDateRangeState',
  default: '12-2023'
})

export const isFullCalendarState: RecoilState<boolean> = atom({
  key: 'isFullCalendar',
  default: true
})

export const isExpandedStatsOverviewState: RecoilState<boolean> = atom({
  key: 'isExpandedStatsOverview',
  default: true
})

export const locationPopupInfoState: RecoilState<LocationPopupInfo | null> = atom({
  key: 'locationPopupInfo',
  default: null as LocationPopupInfo | null
})

export const circleLayerState: RecoilState<CircleLayer> = atom({
  key: 'circleLayer',
  default: circleLayerr
})

export const mapViewStateState: RecoilState<any> = atom({
  key: 'mapViewState',
  default: {
    longitude: 34.37,
    latitude: 31.5,
    zoom: 1
  }
})

export const mapFeatureCollectionState: RecoilState<FeatureCollection<Geometry, GeoJsonProperties>> = atom({
  key: 'mapFeatureCollection',
  default: {
    type: 'FeatureCollection',
    features: [] as Array<Feature<Geometry, GeoJsonProperties>>
  }
})





