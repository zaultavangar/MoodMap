import React from 'react';
import "./OverviewPanel.css";
import { useOverviewPanel} from "~/hooks/useOverviewPanel";
import { MapRef } from "react-map-gl";
import UnfoldLessIcon from "@mui/icons-material/UnfoldLess";
import UnfoldMoreIcon from "@mui/icons-material/UnfoldMore";
import { Box, Paper, Stack, Typography } from "@mui/material";
import { LocationToDetailsMap } from "~/types";

export const OverviewPanel = ({
  mapRef,
}: {
  mapRef: React.RefObject<MapRef>;
}) => {

  const { getOverviewPanelMap, getDateStr, toggleOverviewPanelDisplay } = useOverviewPanel();

  const mostMentioned = getOverviewPanelMap().mostMentioned;
  const mostPostive = getOverviewPanelMap().mostPositive;
  const mostNegative = getOverviewPanelMap().mostNegative;

  const handleLocationClick = (coordinates: number[]) => {
    if (mapRef.current) {
      mapRef.current.flyTo({
        center: [coordinates[0], coordinates[1]],
        zoom: 7,
      });
    }
  };

  const renderTopFiveList = (
    list: LocationToDetailsMap[],
    label: string,
    isCountList = false
  ) => (
    // <Stack direction="row">
    //   <Typography variant="body2">{label}</Typography>
    //   <Stack direction="column">
    //     {list.map((m, idx) => {
    //       const key = Object.keys(m)[0];
    //       const locationName =
    //         key.length >= 16 ? `${key.substring(0, 16)}...` : key;
    //       const value = isCountList
    //         ? m[key].count
    //         : m[key].sentiment.toFixed(2);

    //       return (
    //         <Box
    //           key={idx}
    //           sx={{
    //             cursor: "pointer",
    //             width: "150px",
    //             display: "flex",
    //             flexDirection: "row",
    //             justifyContent: "space-between",
    //             padding: "6px",
    //             gap: "10px",
    //             borderRadius: "5px",
    //           }}
    //           // className="location-container"
    //           onClick={() => handleLocationClick(m[key].coordinates)}
    //         >
    //           <Typography variant="body2" component="p">
    //             {locationName}
    //           </Typography>
    //           <Typography variant="body2" component="p">
    //             {value}
    //           </Typography>
    //         </Box>
    //       );
    //     })}
    //   </Stack>
    // </Stack>
    <div className="top-five-extreme-container">
      <div id="list-header">{label}</div>
      <div className="top-five-list">
        {list.map((m, idx) => {
          const location = Object.keys(m)[0];
          const locationName =
            location.length >= 16 ? `${location.substring(0, 16)}...` : location;
          const value = isCountList
            ? m[location].count
            : m[location].sentiment.toFixed(2);

          return (
            <div
              key={idx}
              className="location-container"
              onClick={() => handleLocationClick(m[location].coordinates)}
            >
              <div id="location-name">{locationName}</div>
              <div>{value}</div>
            </div>
          );
        })}
      </div>
    </div>
  );

  return (
    // <Paper
    //   elevation={2}
    //   sx={{
    //     zIndex: 5,
    //     position: "absolute",
    //     right: "2vw",
    //     bottom: "3vh",
    //     padding: "10px",
    //     gap: "15px",
    //   }}
    // >
    //   {isExpandedStatsOverview ? (
    //     <>
    //       <Stack direction="row" justifyContent="space-between">
    //         <Typography
    //           variant="subtitle1"
    //           component="h6"
    //           fontWeight="bold"
    //           // sx={{ textDecoration: "underline" }}
    //         >
    //           Overview for {getFullDateFromDateStr()}
    //         </Typography>
    //         <UnfoldLessIcon
    //           className="toggle-icon"
    //           onClick={toggleStatsOverviewDisplay}
    //         />
    //       </Stack>
    //       <Stack direction="row" gap="15px">
    //         {renderList(mostMentioned, "Most Mentioned", true)}
    //         {renderList(mostPostive, "Most Positive")}
    //         {renderList(mostNegative.reverse(), "Most Negative")}
    //       </Stack>
    //     </>
    //   ) : (
    //     <Stack
    //       direction="row"
    //       alignItems="center"
    //       gap="5px"
    //       onClick={toggleStatsOverviewDisplay}
    //     >
    //       <Typography variant="body2" component="h6">
    //         See overview for {getFullDateFromDateStr()}
    //       </Typography>
    //       <UnfoldLessIcon className="toggle-icon" />
    //     </Stack>
    //   )}
    // </Paper>
    <div className="stats-overview-container">
      {toggleOverviewPanelDisplay ? (
        <>
          <div className="header-container">
            <div id="stats-overview-title">
              <u>Overview for {getDateStr()}</u>
            </div>
            <UnfoldLessIcon
              className="toggle-icon"
              onClick={toggleOverviewPanelDisplay}
            />
          </div>
          <div className="most-extremes-container">
            {renderTopFiveList(mostMentioned, "Most Mentioned", true)}
            {renderTopFiveList(mostPostive, "Most Positive")}
            {renderTopFiveList(mostNegative.reverse(), "Most Negative")}
          </div>
        </>
      ) : (
        <div
          className="condensed-stats-overview"
          onClick={toggleOverviewPanelDisplay}
        >
          <div>See overview for {getDateStr()}</div>
          <UnfoldMoreIcon className="toggle-icon" />
        </div>
      )}
    </div>
  );
};
