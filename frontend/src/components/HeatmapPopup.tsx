import { Box, Paper, Stack, Typography } from "@mui/material";
import { Popup } from "react-map-gl";
import type { HeatmapInfo } from "~/hooks/useHeatmapPopup";

export type PopupType = 'click' | 'hover';

const HeatmapPopup = ({
  selectedDateRange,
  info,
  onClose,
}: {
  selectedDateRange: string;
  info: HeatmapInfo;
  onClose: () => void;
}) => {
  // TODO: replace with current date
  const sentimentKey = `${selectedDateRange}-sentiment`;
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
            {info.properties && info.properties['location']}
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
              {info.properties && info.properties[sentimentKey].toFixed(2)}
            </Typography>
            <Typography component="p" color="primary">
              Avg. Sentiment
            </Typography>
          </Stack>
          <div>
            {info.articles.length > 0 && info.articles.map((article, idx) => (
              <div key={idx}style={{'marginBottom': '5px'}}>
                <div>
                  <span>{idx+1}. </span>
                  <a href={article.webUrl} target="_blank">{article.webTitle}</a>
                </div>
                
              </div>
            ))}
          </div>
        </Popup>
      </Paper>
    </Box>
  );
};

export default HeatmapPopup;
