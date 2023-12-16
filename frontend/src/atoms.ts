import { RecoilState, atom } from "recoil";
import { LocationPopupInfo } from "./hooks/useLocationPopup";
import { CircleLayer } from "mapbox-gl";
import { circleLayerr } from "./heatmapLayer";
import {
  Feature,
  FeatureCollection,
  GeoJsonProperties,
  Geometry,
} from "geojson";
import { ArticleEntity,  MonthDateInfo } from "./types";


export const searchQueryState: RecoilState<string> = atom({
  key: "searchQuery",
  default: "",
});

export const searchResultsState: RecoilState<ArticleEntity[]> = atom({
  key: "searchResults",
  default: [] as ArticleEntity[]
})

export const searchDateRangeOptionOpenState: RecoilState<boolean> = atom({
  key: 'searchDateRangeOptionOpen',
  default: false
})

export const searchByDateRangeState: RecoilState<boolean> = atom({
  key: 'searchByDateRange',
  default: false
})

export const searchDateFilterPopoverAnchorElState: RecoilState<HTMLButtonElement | null> =
  atom({
    key: "searchDateFilterPopoverAnchorEl",
    default: null as HTMLButtonElement | null,
  });

export const selectedYearState: RecoilState<number> = atom({
  key: "selectedYear",
  default: 2023,
});

export const selectedMonthState: RecoilState<number | undefined> = atom({
  key: 'selectedMonth',
  default: new Date().getMonth()+1 as number | undefined
})

export const isFullCalendarState: RecoilState<boolean> = atom({
  key: "isFullCalendar",
  default: true,
});

export const isExpandedOverviewPanelState: RecoilState<boolean> = atom({
  key: "isExpandedOverviewPanel",
  default: true,
});

export const locationPopupInfoState: RecoilState<LocationPopupInfo | null> =
  atom({
    key: "locationPopupInfo",
    default: null as LocationPopupInfo | null,
  });

export const circleLayerState: RecoilState<CircleLayer> = atom({
  key: "circleLayer",
  default: circleLayerr,
});

export const mapViewStateState: RecoilState<any> = atom({
  key: "mapViewState",
  default: {
    longitude: 34.37,
    latitude: 31.5,
    zoom: 1,
    maxZoom: 12,
  },
});

export const mapFeatureCollectionState: RecoilState<
  FeatureCollection<Geometry, GeoJsonProperties>
> = atom({
  key: "mapFeatureCollection",
  default: {
    type: "FeatureCollection",
    features: [] as Array<Feature<Geometry, GeoJsonProperties>>,
  },
});
