import { Feature, FeatureCollection, GeoJsonProperties } from "geojson";
import { useState } from "react";
import { CircleLayer, HeatmapLayer, type ViewStateChangeEvent } from "react-map-gl";
import { circleLayerr,  updateHeatMapLayer } from "~/components/heatmapLayer";
import { ArticleEntity, FeatureEntity, FrontendApiResponse, handleApiResponse, isSuccessfulResponse } from "~/logic/api";
import { useHeatmapPopup } from "./useHeatmapPopup";
import { PopupType } from "~/components/HeatmapPopup";

// Default location of the map
const ProvidenceLatLong = {
  long: -71.4141362441059,
  lat: 41.82454500035089,
};

export const GazaLatLong = {
  long: 34.47,
  lat: 31.5,
};

export function useMapManager({
  long,
  lat,
}: { long: number; lat: number } = GazaLatLong) {

  const { heatmapInfo, handlePopupOpen, handlePopupClose} = useHeatmapPopup();

  const [mapViewState, setMapViewState] = useState({
    longitude: long,
    latitude: lat,
    zoom: 5,
    // Changes the tilt of the map
    // pitch: 60,
  });

  const [selectedDateRange, setSelectedDateRange] = useState<string>('11-2023');

  const [circleLayer, setCircleLayer] = useState<CircleLayer>(circleLayerr);

  const createFeatureCollection = (features: Feature[]): FeatureCollection => {
    return {
      type: 'FeatureCollection',
      features: features
    }
  }

  const setLayer = (date: string) => {
      const h = updateHeatMapLayer(date, featureCollection.features)
      if (h!== null)
      setCircleLayer(h);
  }

  const [featureCollection, setFeatureCollection] = useState<FeatureCollection>(
    createFeatureCollection([])
  );


  const loadFeatures = async () => {
    const res = await handleApiResponse<'getFeatures', FeatureEntity[]>('getFeatures', {});
    if (isSuccessfulResponse(res)){
      setFeatureCollection(createFeatureCollection(res.data))
    }
  }

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

  const searchByLocation = async (location: string, fromDate: string, toDate: string) => {
    const res = await handleApiResponse<'searchByLocation', ArticleEntity[]>('searchByLocation', {
      location: location,
      fromDate: fromDate, 
      toDate: toDate 
    });
    return res;
  }

  const getArticlesAndOpenPopup = async (
    location: string,
    lng: number,
    lat: number,
    properties: GeoJsonProperties
    ) => {
    const [month, year] = selectedDateRange.split('-').map(Number);
    const firstDay = new Date(year, month - 1, 1); 
    const lastDay = new Date(year, month, 0); // set day to 0 to get last day of the previous month

    // format dates to YYYY-MM-DD
    const fromDate = firstDay.toISOString().split('T')[0];
    const toDate = lastDay.toISOString().split('T')[0];

    const res = await searchByLocation(location, fromDate, toDate)
    if (isSuccessfulResponse(res)){
      handlePopupOpen({
        longitude: lng, 
        latitude: lat, 
        properties: properties, 
        articles: res.data
      });
    } else {
      // throw Exception, set error and display?
    }
  }






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
    heatmapInfo,
    useHeatmapPopup,
    updateHeatMapLayer,
    handlePopupClose
  };
}
