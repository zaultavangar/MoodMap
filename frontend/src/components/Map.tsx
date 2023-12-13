// import { useTheme } from "@mui/material";
import {
  Layer,
  MapLayerMouseEvent,
  MapRef,
  NavigationControl,
  Map as ReactMap,
  ScaleControl,
  Source,
} from "react-map-gl";
import { useMapManager } from "~/hooks/useMapManager";
import LocationPopup from "./locationPopup/LocationPopup";
import React, { useEffect, useRef } from "react";
import { DatePicker } from "./datepicker/DatePicker";
import { useRecoilValue } from "recoil";
import { locationPopupInfoState } from "~/atoms";
import { StatsOverview } from "./statsOverview/StatsOverview";

// Accesing the mapbox API token
const MAPBOX_API_TOKEN = import.meta.env.VITE_MAPBOX_API_TOKEN;

const COLOR_MODE = import.meta.env.VITE_COLOR_MODE;

/**
 * Map component displays a heatmap of the sentiment of a topic in a given geographical area
 */
const Map = () => {
  const {
    mapViewState,
    handleMapMove,
    loadFeatures,
    featureCollection,
    circleLayer,
    setLayer,
    selectedDateRange,
    getArticlesAndOpenPopup,
    handlePopupClose,
  } = useMapManager();

  const locationPopupInfo = useRecoilValue(locationPopupInfoState);

  const _mapRef = useRef<MapRef>(null);

  // for map click and hover operaations
  const handleMouseEventOperation = async (e: MapLayerMouseEvent) => {
    e.preventDefault();

    const area = e.features && e.features[0];

    if (!area) {
      return;
    }
    // get articles for that feature from backend
    if (
      area.properties !== undefined &&
      area.properties !== null &&
      area.properties["location"] !== null &&
      area.properties["location"] !== null
    ) {
      if (_mapRef.current) {
        _mapRef.current.flyTo({
          center: [e.lngLat.lng, e.lngLat.lat - 1],
        });
      }
      await getArticlesAndOpenPopup(
        area.properties["location"],
        e.lngLat.lng,
        e.lngLat.lat,
        area.properties
      );
    }
  };

  useEffect(() => {
    console.log("hello");
    loadFeatures();
  }, []);

  useEffect(() => {
    setLayer(selectedDateRange);
  }, [featureCollection]);

  useEffect(() => {
    setLayer(selectedDateRange);
  }, [selectedDateRange]);

  return (
    <main>
      <ReactMap
        ref={_mapRef}
        mapboxAccessToken={MAPBOX_API_TOKEN}
        {...mapViewState}
        minZoom={1}
        maxZoom={8}
        data-testid="map"
        interactiveLayerIds={["heatmap"]}
        mapStyle={`mapbox://styles/mapbox/${COLOR_MODE}-v10`}
        // mapStyle={`mapbox://styles/mapbox/${theme.palette.mode}-v11`}
        style={{
          position: "absolute",
          top: 0,
          left: 0,
          bottom: 0,
          zIndex: 0,
          width: "100vw",
          height: "100vh",
          overflow: "hidden",
        }}
        onMove={handleMapMove}
        onClick={handleMouseEventOperation}
      >
        <Source type="geojson" data={featureCollection}>
          <Layer {...circleLayer} />
        </Source>
        {locationPopupInfo && (
          <LocationPopup info={locationPopupInfo} onClose={handlePopupClose} />
        )}
        <StatsOverview mapRef={_mapRef} />
        <NavigationControl position="bottom-left" />
        <ScaleControl />
      </ReactMap>
    </main>
  );
};

export default Map;
