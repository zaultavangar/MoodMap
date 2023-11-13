import { useState } from "react";
import type { ViewStateChangeEvent } from "react-map-gl";

// Default location of the map
const ProvidenceLatLong = {
  long: -71.4141362441059,
  lat: 41.82454500035089,
};

export function useMapView({
  long,
  lat,
}: { long: number; lat: number } = ProvidenceLatLong) {
  const [mapViewState, setMapViewState] = useState({
    longitude: long,
    latitude: lat,
    zoom: 5,
    // Changes the tilt of the map
    // pitch: 60,
  });

  // Handling the map move
  const handleMapMove = (e: ViewStateChangeEvent) => {
    setMapViewState(e.viewState);
  };

  return {
    mapViewState,
    handleMapMove,
  };
}
