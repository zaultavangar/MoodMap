import { Feature, FeatureCollection, GeoJsonProperties } from "geojson";
import { type ViewStateChangeEvent } from "react-map-gl";
import { updateHeatMapLayer } from "~/components/heatmapLayer";
import {
  ArticleEntity,
  FeatureEntity,
  handleApiResponse,
  isSuccessfulResponse,
} from "~/logic/api";
import { useLocationPopup } from "./useLocationPopup";
import { useRecoilState, useRecoilValue } from "recoil";
import {
  circleLayerState,
  locationPopupInfoState,
  mapFeatureCollectionState,
  mapViewStateState,
  selectedDateRangeState,
} from "~/atoms";

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

  const selectedDateRange = useRecoilValue(selectedDateRangeState);

  const [mapViewState, setMapViewState] = useRecoilState(mapViewStateState);

  const [circleLayer, setCircleLayer] = useRecoilState(circleLayerState);

  const createFeatureCollection = (features: Feature[]): FeatureCollection => {
    return {
      type: "FeatureCollection",
      features: features,
    };
  };

  const setLayer = (date: string) => {
    const h = updateHeatMapLayer(date, featureCollection.features);
    if (h !== null) setCircleLayer(h);
  };

  const [featureCollection, setFeatureCollection] = useRecoilState(
    mapFeatureCollectionState
  );

  const loadFeatures = async () => {
    const res = await handleApiResponse<"getFeatures", FeatureEntity[]>(
      "getFeatures",
      {}
    );
    if (isSuccessfulResponse(res)) {
      setFeatureCollection(createFeatureCollection(res.data));
    }
  };

  // const openPopup = (
  //   lng: number,
  //   lat: number,
  //   properties: GeoJsonProperties,
  //   articles: ArticleEntity[]) => {
  //   handlePopupOpen({
  //     popupType: popupType,
  //     longitude: lng,
  //     latitude: lat,
  //     properties: properties,
  //     articles: articles
  //   });
  // }

  const searchByLocation = async (
    location: string,
    fromDate: string,
    toDate: string
  ) => {
    const res = await handleApiResponse<"searchByLocation", ArticleEntity[]>(
      "searchByLocation",
      {
        location: location,
        fromDate: fromDate,
        toDate: toDate,
      }
    );
    return res;
  };

  const getArticlesAndOpenPopup = async (
    location: string,
    lng: number,
    lat: number,
    properties: GeoJsonProperties
  ) => {
    const [month, year] = selectedDateRange.split("-").map(Number);
    const firstDay = new Date(year, month - 1, 1);
    const lastDay = new Date(year, month, 0); // set day to 0 to get last day of the previous month

    // format dates to YYYY-MM-DD
    const fromDate = firstDay.toISOString().split("T")[0];
    const toDate = lastDay.toISOString().split("T")[0];

    const res = await searchByLocation(location, fromDate, toDate);
    if (isSuccessfulResponse(res)) {
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
    selectedDateRange,
    getArticlesAndOpenPopup,
    updateHeatMapLayer,
    handlePopupClose,
  };
}
