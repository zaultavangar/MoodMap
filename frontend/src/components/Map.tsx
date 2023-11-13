// import { useTheme } from "@mui/material";
import { Map as ReactMap } from "react-map-gl";
import { useMapView } from "~/hooks/useMapView";

// Accesing the mapbox API token
const MAPBOX_API_TOKEN = import.meta.env.VITE_MAPBOX_API_TOKEN;

const COLOR_MODE = import.meta.env.VITE_COLOR_MODE;

/**
 * Map component displays a heatmap of the sentiment of a topic in a given geographical area
 */
const Map = () => {
  // const {theme} = useTheme();
  const { mapViewState, handleMapMove } = useMapView();
  return (
    <main>
      <ReactMap
        mapboxAccessToken={MAPBOX_API_TOKEN}
        {...mapViewState}
        data-testid="map"
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
      ></ReactMap>
    </main>
  );
};

export default Map;
