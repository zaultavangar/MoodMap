// import { useTheme } from "@mui/material";
import {
  Layer,
  MapLayerMouseEvent,
  NavigationControl,
  Map as ReactMap,
  ScaleControl,
  Source,
} from "react-map-gl";
import { useMapManager } from "~/hooks/useMapManager";
import HeatmapPopup from "./HeatmapPopup";
import { useEffect } from "react";

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
    heatmapInfo,
    handlePopupClose,
   } = useMapManager();


  // for map click and hover operaations
  const handleMouseEventOperation = async (
    e: MapLayerMouseEvent) => {
      
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
          await getArticlesAndOpenPopup (
            area.properties['location'],
            e.lngLat.lng,
            e.lngLat.lat,
            area.properties)
          }
        
    }
    // TODO: Do Zod runtime validation here so there's no issues with the popup
    
  

  useEffect(() => {
    console.log("hello");
    loadFeatures();
  }, [])

  useEffect(() => {
    setLayer(selectedDateRange);
  }, [featureCollection])


  return (
    <main>
      <ReactMap
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
        {heatmapInfo && (
          <HeatmapPopup selectedDateRange={selectedDateRange} info={heatmapInfo} onClose={handlePopupClose} />
        )}
         <NavigationControl position="bottom-left"/>
         <ScaleControl />
      </ReactMap>
    </main>
  );
};

export default Map;
