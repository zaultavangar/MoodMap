import { Feature, FeatureCollection, GeoJsonProperties } from "geojson";
import { type ViewStateChangeEvent } from "react-map-gl";
import { updateHeatMapLayer } from "~/heatmapLayer";
import api from "~logic/api";
import { isSuccessfulResponse } from "~/logic/api";
import { useLocationPopup } from "./useLocationPopup";
import { useRecoilState, useRecoilValue, useSetRecoilState } from "recoil";
import {
  circleLayerState,
  isExpandedOverviewPanelState,
  mapFeatureCollectionState,
  mapViewStateState,
  selectedMonthState,
  selectedYearState,
} from "~/atoms";
import { ArticleEntity, FeatureEntity } from "~/types";

// Default location of the map
// const ProvidenceLatLong = {
//   long: -71.4141362441059,
//   lat: 41.82454500035089,
// };

export const GazaLatLong = {
  long: 34.47,
  lat: 31.5,
};

export function useMapManager() {
  const { handlePopupOpen, handlePopupClose } = useLocationPopup();

  const selectedYear = useRecoilValue(selectedYearState);
  const selectedMonth = useRecoilValue(selectedMonthState);

  const [mapViewState, setMapViewState] = useRecoilState(mapViewStateState);

  const [circleLayer, setCircleLayer] = useRecoilState(circleLayerState);

  const setIsExpandedOverviewPanel = useSetRecoilState(isExpandedOverviewPanelState);

  const setLayer = () => {
    console.log(featureCollection.features);
    const h = updateHeatMapLayer(
      featureCollection.features,
      selectedYear,
      selectedMonth
    );
    if (h !== null) setCircleLayer(h);
  };

  const [featureCollection, setFeatureCollection] = useRecoilState(
    mapFeatureCollectionState
  );

  const loadFeatures = async () => {
    const res = await api.getFeatures();
    // const res = await handleApiResponse<"getFeatures", FeatureEntity[]>(
    //   "getFeatures",
    //   {}
    // );
    if (isSuccessfulResponse(res)) {
      setFeatureCollection(createFeatureCollection(res.data));
    }
  };

  const searchByLocation = async (
    location: string,
    fromDate: string,
    toDate: string
  ) => {
    const res = await api.searchByLocation(location, fromDate, toDate);
    // const res = await handleApiResponse<"searchByLocation", ArticleEntity[]>(
    //   "searchByLocation",
    //   {
    //     location: location,
    //     fromDate: fromDate,
    //     toDate: toDate,
    //   }
    // );
    return res;
  };

  const getArticlesAndOpenPopup = async (
    location: string,
    lng: number,
    lat: number,
    properties: GeoJsonProperties
  ) => {
    const month = selectedMonth ? selectedMonth : 1;
    const firstDay = new Date(selectedYear, month - 1, 1);
    const lastDay = new Date(
      selectedYear,
      selectedMonth ? month + 1 : month + 12,
      0
    ); // set day to 0 to get last day of the previous month

  // format dates to YYYY-MM-DD
    const fromDate = firstDay.toISOString().split("T")[0];
    const toDate = lastDay.toISOString().split("T")[0];

    const res = await searchByLocation(location, fromDate, toDate);
    
    if (isSuccessfulResponse(res)) {
      setIsExpandedOverviewPanel(false);
      handlePopupOpen({
        longitude: lng,
        latitude: lat,
        properties: properties,
        articles: res.data,
      });
    } else {
      // throw Exception, set error and display?
    }
  };

  // Handling the map move
  const handleMapMove = (e: ViewStateChangeEvent) => {
    setMapViewState(e.viewState);
  };

  return {
    mapViewState,
    handleMapMove,
    loadFeatures,
    featureCollection,
    setFeatureCollection,
    circleLayer,
    setLayer,
    getArticlesAndOpenPopup,
    updateHeatMapLayer,
    handlePopupClose,
  };
}

export function createFeatureCollection(
  features: Feature[]
): FeatureCollection {
  return {
    type: "FeatureCollection",
    features: features,
  };
}
