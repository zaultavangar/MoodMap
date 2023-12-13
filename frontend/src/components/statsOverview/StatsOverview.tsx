import { useRecoilState, useRecoilValue } from "recoil";
import "./StatsOverview.css";
import { isExpandedStatsOverviewState, selectedDateRangeState } from "~/atoms";
import {
  LocationToDetailsMap,
  useStatsOverview,
} from "~/hooks/useStatsOverview";
import { MapRef } from "react-map-gl";
import UnfoldLessIcon from "@mui/icons-material/UnfoldLess";
import UnfoldMoreIcon from "@mui/icons-material/UnfoldMore";
import { Box, Paper, Stack, Typography } from "@mui/material";

export const StatsOverview = ({
  mapRef,
}: {
  mapRef: React.RefObject<MapRef>;
}) => {
  const selectedDateRange = useRecoilValue(selectedDateRangeState);
  const [isExpandedStatsOverview, setIsExpandedStatsOverview] = useRecoilState(
    isExpandedStatsOverviewState
  );

  const { getStatsOverviewMap } = useStatsOverview();

  const mostMentioned = getStatsOverviewMap().mostMentioned;
  const mostPostive = getStatsOverviewMap().mostPositive;
  const mostNegative = getStatsOverviewMap().mostNegative;

  const getFullDateFromDateStr = () => {
    const year = parseInt(selectedDateRange.substring(3, 7));
    const month = parseInt(selectedDateRange.substring(0, 2)) - 1;

    const date = new Date(year, month);
    return date.toLocaleString("default", { month: "short", year: "numeric" });
  };

  const handleLocationClick = (coordinates: number[]) => {
    console.error("Coords: ", coordinates);
    console.error(mapRef.current);
    if (mapRef.current) {
      mapRef.current.flyTo({
        center: [coordinates[0], coordinates[1]],
        zoom: 12,
      });
    }
  };

  const toggleStatsOverviewDisplay = () => {
    setIsExpandedStatsOverview(!isExpandedStatsOverview);
  };

  const renderList = (
    list: LocationToDetailsMap[],
    label: string,
    isCountList = false
  ) => (
    <Stack direction="row">
      <Typography variant="body2">{label}</Typography>
      <Stack direction="column">
        {list.map((m, idx) => {
          const key = Object.keys(m)[0];
          const locationName =
            key.length >= 16 ? `${key.substring(0, 16)}...` : key;
          const value = isCountList
            ? m[key].count
            : m[key].sentiment.toFixed(2);

          return (
            <Box
              key={idx}
              sx={{
                cursor: "pointer",
                width: "150px",
                display: "flex",
                flexDirection: "row",
                justifyContent: "space-between",
                padding: "6px",
                gap: "10px",
                borderRadius: "5px",
              }}
              // className="location-container"
              onClick={() => handleLocationClick(m[key].coordinates)}
            >
              <Typography variant="body2" component="p">
                {locationName}
              </Typography>
              <Typography variant="body2" component="p">
                {value}
              </Typography>
            </Box>
          );
        })}
      </Stack>
    </Stack>
    // <div className="top-five-extreme-container">
    //   <div id="list-header">{label}</div>
    //   <div className="top-five-list">
    //     {list.map((m, idx) => {
    //       const key = Object.keys(m)[0];
    //       const locationName =
    //         key.length >= 16 ? `${key.substring(0, 16)}...` : key;
    //       const value = isCountList
    //         ? m[key].count
    //         : m[key].sentiment.toFixed(2);

    //       return (
    //         <div
    //           key={idx}
    //           className="location-container"
    //           onClick={() => handleLocationClick(m[key].coordinates)}
    //         >
    //           <div id="location-name">{locationName}</div>
    //           <div>{value}</div>
    //         </div>
    //       );
    //     })}
    //   </div>
    // </div>
  );

  return (
    <Paper
      elevation={2}
      sx={{
        zIndex: 5,
        position: "absolute",
        right: "2vw",
        bottom: "3vh",
        padding: "10px",
        gap: "15px",
      }}
    >
      {isExpandedStatsOverview ? (
        <>
          <Stack direction="row" justifyContent="space-between">
            <Typography
              variant="subtitle1"
              component="h6"
              fontWeight="bold"
              // sx={{ textDecoration: "underline" }}
            >
              Overview for {getFullDateFromDateStr()}
            </Typography>
            <UnfoldLessIcon
              className="toggle-icon"
              onClick={toggleStatsOverviewDisplay}
            />
          </Stack>
          <Stack direction="row" gap="15px">
            {renderList(mostMentioned, "Most Mentioned", true)}
            {renderList(mostPostive, "Most Positive")}
            {renderList(mostNegative.reverse(), "Most Negative")}
          </Stack>
        </>
      ) : (
        <Stack
          direction="row"
          alignItems="center"
          gap="5px"
          onClick={toggleStatsOverviewDisplay}
        >
          <Typography variant="body2" component="h6">
            See overview for {getFullDateFromDateStr()}
          </Typography>
          <UnfoldLessIcon className="toggle-icon" />
        </Stack>
      )}
    </Paper>
    // <div className="stats-overview-container">
    //   {isExpandedStatsOverview ? (
    //     <>
    //       <div className="header-container">
    //         <div id="stats-overview-title">
    //           <u>Overview for {getFullDateFromDateStr()}</u>
    //         </div>
    //         <UnfoldLessIcon
    //           className="toggle-icon"
    //           onClick={toggleStatsOverviewDisplay}
    //         />
    //       </div>
    //       <div className="most-extremes-container">
    //         {renderList(mostMentioned, "Most Mentioned", true)}
    //         {renderList(mostPostive, "Most Positive")}
    //         {renderList(mostNegative.reverse(), "Most Negative")}
    //       </div>
    //     </>
    //   ) : (
    //     <div
    //       className="condensed-stats-overview"
    //       onClick={toggleStatsOverviewDisplay}
    //     >
    //       <div>See overview for {getFullDateFromDateStr()}</div>
    //       <UnfoldMoreIcon className="toggle-icon" />
    //     </div>
    //   )}
    // </div>
  );
};
