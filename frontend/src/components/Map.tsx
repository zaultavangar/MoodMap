// import { useTheme } from "@mui/material";
import {
  Layer,
  MapLayerMouseEvent,
  NavigationControl,
  Map as ReactMap,
  ScaleControl,
  Source,
} from "react-map-gl";
import { mockGeojson } from "~/data/mockGeojson";
import { useHeatmapPopup } from "~/hooks/useHeatmapPopup";
import { useMapManager } from "~/hooks/useMapManager";
import HeatmapPopup from "./HeatmapPopup";
import { useEffect } from "react";
import { ArticleEntity, handleApiResponse, isSuccessfulResponse } from "~/logic/api";

// Accesing the mapbox API token
const MAPBOX_API_TOKEN = import.meta.env.VITE_MAPBOX_API_TOKEN;

const COLOR_MODE = import.meta.env.VITE_COLOR_MODE;

/**
 * Map component displays a heatmap of the sentiment of a topic in a given geographical area
 */
const Map = () => {
  // const {theme} = useTheme();
  const { 
    mapViewState, 
    handleMapMove, 
    loadFeatures, 
    featureCollection,
    circleLayer,
    setLayer
   } = useMapManager();
  const { heatmapInfo, handlePopupOpen, handlePopupClose } = useHeatmapPopup();

  const handleMapClick = async (e: MapLayerMouseEvent) => {
    e.preventDefault();
    const area = e.features && e.features[0];
    console.error(area);
    if (!area) {
      return;
    }
    // get articles for that feature from backend 
    if (area.properties !== undefined && 
      area.properties !== null &&
      area.properties['location'] !== null && 
      area.properties['location'] !== null){
        const res = await handleApiResponse<'searchByLocation', ArticleEntity[]>('searchByLocation', {
          location: area.properties['location'],
          fromDate: "2023-11-01", // TODO: CHANGE
          toDate: "2023-11-30" // TODO: CHANGE
        });
        if (isSuccessfulResponse(res)){
          handlePopupOpen({
            longitude: e.lngLat.lng,
            latitude: e.lngLat.lat,
            properties: area.properties,
            articles: res.data,
          });
        }
    }
    // TODO: Do Zod runtime validation here so there's no issues with the popup
    
  };

  useEffect(() => {
    console.log("hello");
    loadFeatures();
  }, [])

  useEffect(() => {
    setLayer("11-2023");
  }, [featureCollection])


  return (
    <main>
      <ReactMap
        mapboxAccessToken={MAPBOX_API_TOKEN}
        {...mapViewState}
        minZoom={1}
        maxZoom={16}
        data-testid="map"
        interactiveLayerIds={["heatmap"]}
        mapStyle={`mapbox://styles/mapbox/${COLOR_MODE}-v11`}
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
        onClick={handleMapClick}
      >
        <Source type="geojson" data={featureCollection}>
          <Layer {...circleLayer} />
        </Source>
        {heatmapInfo && (
          <HeatmapPopup info={heatmapInfo} onClose={handlePopupClose} />
        )}
         <NavigationControl />
         <ScaleControl />
      </ReactMap>
    </main>
  );
};

export default Map;
