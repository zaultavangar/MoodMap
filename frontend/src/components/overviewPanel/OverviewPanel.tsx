import React, { useEffect } from "react";
import "./OverviewPanel.css";
import { useOverviewPanel } from "~/hooks/useOverviewPanel";
import { MapRef } from "react-map-gl";
import UnfoldLessIcon from "@mui/icons-material/UnfoldLess";
import UnfoldMoreIcon from "@mui/icons-material/UnfoldMore";
import CircleIcon from "@mui/icons-material/Circle";
import { Grid, Link, Stack, Typography } from "@mui/material";
import { LocationToDetailsMap } from "~/types";
import { useRecoilValue } from "recoil";
import { searchResultsState } from "~/atoms";
import * as d3 from "d3";

export const OverviewPanel = ({
  mapRef,
}: {
  mapRef: React.RefObject<MapRef>;
}) => {
  const {
    getOverviewPanelMap,
    getDateStr,
    isExpandedOverviewPanel,
    setIsExpandedOverviewPanel,
    toggleOverviewPanelDisplay,
  } = useOverviewPanel();

  const mostMentioned = getOverviewPanelMap().mostMentioned;
  const mostPostive = getOverviewPanelMap().mostPositive;
  const mostNegative = getOverviewPanelMap().mostNegative;

  const searchResults = useRecoilValue(searchResultsState);

  useEffect(() => {
    if (searchResults.length > 0) {
      setIsExpandedOverviewPanel(false);
    }
  }, [searchResults]);

  const handleLocationClick = (coordinates: number[]) => {
    if (mapRef.current) {
      mapRef.current.flyTo({
        center: [coordinates[0], coordinates[1]],
        zoom: 7,
      });
    }
  };

  const colorScale = d3.scaleLinear(
    [0, 0.2, 0.5, 0.8, 1], // input values
    ["red", "red", "yellow", "green", "green"] // output colors
  );

  const renderTopFiveList = (
    list: LocationToDetailsMap[],
    label: string,
    isCountList = false
  ) => (
    <Grid container direction="column" spacing={1}>
      <Grid item>
        <Typography variant="subtitle1" component="h6" fontSize={"1.3em"}>
          {label}
        </Typography>
      </Grid>
      <Grid item>
        {list.map((m, idx) => {
          const location = Object.keys(m)[0];
          const locationName =
            location.length >= 16
              ? `${location.substring(0, 16)}...`
              : location;
          const sentimentScore = m[location].sentiment;
          const value = isCountList
            ? m[location].count
            : sentimentScore.toFixed(2);

          return (
            <Stack
              key={idx}
              className="location-container"
              direction="row"
              // justifyContent="space-between"
              alignItems="center"
              spacing={2}
              sx={{ padding: "10px" }}
              onClick={() => handleLocationClick(m[location].coordinates)}
            >
              <Typography variant="body2" component="p" sx={{ flex: 1 }}>
                {locationName}
              </Typography>
              {isCountList && (
                <CircleIcon
                  style={{
                    color: colorScale(sentimentScore),
                    fontSize: "10px",
                  }}
                />
              )}
              <Typography variant="body2" component="p">
                {value}
              </Typography>
            </Stack>
          );
        })}
      </Grid>
    </Grid>
  );

  return (
    // <>
    <Grid
      container
      className="outer-container"
      direction="column"
      data-testid="overview-panel"
      justifyContent="center"
      alignItems={isExpandedOverviewPanel ? "flex-start" : "center"}
      spacing={2}
      borderRadius={5}
      sx={{
        zIndex: 5,
        position: "absolute",
        right: "2vw",
        bottom: "3vh",
        backgroundColor: "grey",
        width: "max-content",
        maxWidth: "70vw",
        padding: isExpandedOverviewPanel ? "0px" : "5px",
        paddingBottom: isExpandedOverviewPanel ? "5px" : "0px",
        paddingRight: "5px",
      }}
    >
      {isExpandedOverviewPanel ? (
        <>
          <Grid item width="100%">
            <Stack
              className="header-container"
              direction="row"
              justifyContent="space-between"
              alignItems="center"
            >
              <Typography
                variant="subtitle1"
                component="h6"
                fontWeight="bold"
                fontSize={"1.4em"}
              >
                Overview for {getDateStr()}
              </Typography>
              <UnfoldLessIcon
                className="toggle-icon"
                data-testid="overview-minimize-button"
                onClick={toggleOverviewPanelDisplay}
              />
            </Stack>
          </Grid>
          <Grid item>
            <Grid
              container
              direction="row"
              justifyContent="flex-start"
              alignItems="center"
              spacing={2}
              id="overview-categories"
              data-test-id="overview-categories"
            >
              <Grid item>
                {renderTopFiveList(mostMentioned, "Most Mentioned", true)}
              </Grid>
              <Grid item>
                {renderTopFiveList(mostPostive, "Most Positive")}
              </Grid>
              <Grid item>
                {renderTopFiveList(mostNegative.reverse(), "Most Negative")}
              </Grid>
            </Grid>
          </Grid>
          <Grid item>
            <Typography variant="caption" component="p">
              All articles sourced from{" "}
              <Link
                href="https://open-platform.theguardian.com/explore/"
                target="_blank"
                color={"#6f19d2"}
              >
                The Guardian Open Platform
              </Link>
            </Typography>
          </Grid>
        </>
      ) : (
        <Grid className="unfold-more-container" item>
          <UnfoldMoreIcon
            className="toggle-icon"
            data-testid="overview-maximize-button"
            onClick={toggleOverviewPanelDisplay}
          />
        </Grid>
      )}
    </Grid>
  );
};
