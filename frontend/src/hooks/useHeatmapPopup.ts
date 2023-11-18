import { useState } from "react";

export type HeatmapInfo = {
  longitude: number;
  latitude: number;
  sentiment: number;
};

/**
 * A custom React hook that helps display a popup on the map given a certain redlined area
 */
export function useHeatmapPopup() {
  const [heatmapInfo, setHeatmapInfo] = useState<HeatmapInfo | null>(null);

  // Handling the Opening the popup
  const handlePopupOpen = (area: HeatmapInfo) => {
    setHeatmapInfo(area);
  };

  // Handling the popup close
  const handlePopupClose = () => {
    setHeatmapInfo(null);
  };

  return {
    heatmapInfo,
    handlePopupOpen,
    handlePopupClose,
  };
}
