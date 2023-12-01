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
import { useMapView } from "~/hooks/useMapView";
import HeatmapPopup from "./HeatmapPopup";
import { heatmapLayer } from "./heatmapLayer";

// Accesing the mapbox API token
const MAPBOX_API_TOKEN = import.meta.env.VITE_MAPBOX_API_TOKEN;

const COLOR_MODE = import.meta.env.VITE_COLOR_MODE;

/**
 * Map component displays a heatmap of the sentiment of a topic in a given geographical area
 */
const Map = () => {
  // const {theme} = useTheme();
  const { mapViewState, handleMapMove } = useMapView();
  const { heatmapInfo, handlePopupOpen, handlePopupClose } = useHeatmapPopup();
  const handleMapClick = (e: MapLayerMouseEvent) => {
    e.preventDefault();
    const area = e.features && e.features[0];
    if (!area) {
      return;
    }
    // TODO: Do Zod runtime validation here so there's no issues with the popup
    handlePopupOpen({
      longitude: e.lngLat.lng,
      latitude: e.lngLat.lat,
      region: area.properties?.region,
      articles: area.properties?.articles,
      sentiment: area.properties?.sentiment,
    });
  };
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
        <Source type="geojson" data={mockGeojson}>
          <Layer {...heatmapLayer} />
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
