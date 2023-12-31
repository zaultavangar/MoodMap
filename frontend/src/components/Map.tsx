import React, { useEffect, useRef } from "react";
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
import { useRecoilValue, useSetRecoilState } from "recoil";
import {
  locationPopupInfoState,
  searchDateRangeOptionOpenState,
  searchQueryState,
  searchResultsState,
  selectedMonthState,
  selectedYearState,
} from "~/atoms";
import { OverviewPanel } from "./overviewPanel/OverviewPanel";

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
    getArticlesAndOpenPopup,
    handlePopupClose,
  } = useMapManager();

  const locationPopupInfo = useRecoilValue(locationPopupInfoState);
  const selectedYear = useRecoilValue(selectedYearState);
  const selectedMonth = useRecoilValue(selectedMonthState);

  const setSearchQuery = useSetRecoilState(searchQueryState);
  const setSearchDateRangeOptionOpen = useSetRecoilState(
    searchDateRangeOptionOpenState
  );

  const _mapRef = useRef<MapRef>(null);

  // for map click and hover operaations
  const handleMouseEventOperation = async (e: MapLayerMouseEvent) => {
    e.preventDefault();

    console.error("hi");
    setSearchQuery("");
    setSearchDateRangeOptionOpen(false);

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
          center: [e.lngLat.lng, e.lngLat.lat],
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
    loadFeatures();
  }, []);

  useEffect(() => {
    setLayer();
  }, [featureCollection, selectedMonth, selectedYear]);

  return (
    <main>
      {/** Displays the map */}
      <ReactMap
        ref={_mapRef}
        mapboxAccessToken={MAPBOX_API_TOKEN}
        {...mapViewState}
        minZoom={1}
        maxZoom={8}
        id="map"
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
        {/** Displays the heatmap locations */}
        <Source type="geojson" data={featureCollection}>
          <Layer {...circleLayer} />
        </Source>
        {locationPopupInfo && (
          <LocationPopup info={locationPopupInfo} onClose={handlePopupClose} />
        )}
        {/** Displays the overview panel */}
        <OverviewPanel mapRef={_mapRef} />
        {/** Allows the user to zoom in and out using keyboard controls */}
        <NavigationControl position="bottom-left" />
        {/** Displays the scale of the map */}
        <ScaleControl />
      </ReactMap>
    </main>
  );
};

export default Map;
