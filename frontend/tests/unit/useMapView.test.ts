import { renderHook } from "@testing-library/react-hooks";
import { describe, expect, it } from "vitest";
import { GazaLatLong, useMapView } from "../../src/hooks/useMapView";

describe("useMapView hook", () => {
  it("should initialize with default values", () => {
    const { result } = renderHook(() => useMapView());

    expect(result.current.mapViewState.longitude).toBe(GazaLatLong.long);
    expect(result.current.mapViewState.latitude).toBe(GazaLatLong.lat);
    expect(result.current.mapViewState.zoom).toBe(5);
  });

  it("should initialize with provided values", () => {
    const customCoordinates = { long: -80, lat: 35 };
    const { result } = renderHook(() => useMapView(customCoordinates));

    expect(result.current.mapViewState.longitude).toBe(customCoordinates.long);
    expect(result.current.mapViewState.latitude).toBe(customCoordinates.lat);
    expect(result.current.mapViewState.zoom).toBe(5);
  });

  it("should update mapViewState on handleMapMove", () => {
    const { result } = renderHook(() => useMapView());

    // const mockViewStateChange: ViewStateChangeEvent = {
    //   type: "move",

    // }
    // {
    //   type: "move",
    //   viewState: {
    //     longitude: -75,
    //     latitude: 40,
    //     zoom: 8,
    //     pitch: 0,
    //     bearing: 0,
    //     padding: {
    //       top: 0,
    //       bottom: 0,
    //       left: 0,
    //       right: 0,
    //     },
    //   },
    // };

    // act(() => result.current.handleMapMove(mockViewStateChange));

    expect(result.current.mapViewState.longitude).toBe(-75);
    expect(result.current.mapViewState.latitude).toBe(40);
    expect(result.current.mapViewState.zoom).toBe(8);
  });
});
