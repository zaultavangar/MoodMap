import { GeoJsonProperties } from "geojson";
import { useRecoilState, useSetRecoilState } from "recoil";
import { locationPopupInfoState } from "~/atoms";
import { ArticleEntity } from "~/logic/api";

export type LocationPopupInfo = {
  articles: ArticleEntity[];
  longitude: number;
  latitude: number;
  properties: GeoJsonProperties;
};

/**
 * A custom React hook that helps display a popup on the map given a certain redlined area
 */
export function useLocationPopup() {
  const setLocationPopupInfo = useSetRecoilState(locationPopupInfoState);

  // Handling the Opening the popup
  const handlePopupOpen = (area: LocationPopupInfo) => {
    setLocationPopupInfo(area);
  };

  // Handling the popup close
  const handlePopupClose = () => {
    setLocationPopupInfo(null);
  };

  return {
    handlePopupOpen,
    handlePopupClose,
  };
}
