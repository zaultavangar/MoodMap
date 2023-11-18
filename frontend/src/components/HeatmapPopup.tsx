import { Box, Paper, Stack, Typography } from "@mui/material";
import { Popup } from "react-map-gl";
import type { HeatmapInfo } from "~/hooks/useHeatmapPopup";

const HeatmapPopup = ({
  info,
  onClose,
}: {
  info: HeatmapInfo;
  onClose: () => void;
}) => {
  return (
    <Box sx={{ minWidth: "300px", maxWidth: "500px" }}>
      <Paper>
        <Popup
          longitude={info.longitude}
          latitude={info.latitude}
          anchor="top"
          onClose={onClose}
          closeOnClick={false}
          closeOnMove={false}
        >
          <Typography variant="h6" component="h3" fontWeight="bold">
            {info.region}
          </Typography>
          {/* <Typography variant="subtitle1" component="h4" fontWeight="bold">
            Articles
          </Typography>
          {info.articles.map((article) => (
            <>
              <Typography variant="subtitle2" component="h5">
                {article.title}
              </Typography>
              <Typography component="p">{article.description}</Typography>
            </>
          ))} */}
          <Stack>
            <Typography
              variant="h3"
              component="p"
              fontWeight="bold"
              color="primary"
            >
              {info.sentiment}
            </Typography>
            <Typography component="p" color="primary">
              Avg. Sentiment
            </Typography>
          </Stack>
        </Popup>
      </Paper>
    </Box>
  );
};

export default HeatmapPopup;
