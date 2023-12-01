import { Feature, FeatureCollection } from "geojson";
import { useState } from "react";
import { CircleLayer, HeatmapLayer, type ViewStateChangeEvent } from "react-map-gl";
import { circleLayerr,  updateHeatMapLayer } from "~/components/heatmapLayer";
import { FeatureEntity, FrontendApiResponse, handleApiResponse, isSuccessfulResponse } from "~/logic/api";

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
  const [mapViewState, setMapViewState] = useState({
    longitude: long,
    latitude: lat,
    zoom: 5,
    // Changes the tilt of the map
    // pitch: 60,
  });

  const [circleLayer, setCircleLayer] = useState<CircleLayer>(circleLayerr);

  const createFeatureCollection = (features: Feature[]): FeatureCollection => {
    return {
      type: 'FeatureCollection',
      features: features
    }
  }

  const setLayer = (date: string) => {
      setCircleLayer(updateHeatMapLayer(date, featureCollection.features));
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
    setLayer
  };
}
