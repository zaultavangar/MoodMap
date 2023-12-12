import { useEffect, useRef } from 'react';
import './LocationPopup.css';
import { Box, Paper, Stack, Typography } from "@mui/material";
import { Popup } from "react-map-gl";
import { useRecoilValue } from "recoil";
import { selectedDateRangeState } from "~/atoms";
import type { LocationPopupInfo } from "~/hooks/useLocationPopup";
import mapboxgl from 'mapbox-gl';


export type PopupType = 'click' | 'hover';

const LocationPopup = ({
  info,
  onClose,
}: {
  info: LocationPopupInfo
  onClose: () => void;
}) => {
  const selectedDateRange = useRecoilValue(selectedDateRangeState);
  const sentimentKey = `${selectedDateRange}-sentiment`;
  const scrollRef = useRef<mapboxgl.Popup>(null);

  useEffect(() => {
    if (scrollRef.current) {
      scrollRef.current.getElement().scrollTop = 0; // scroll to top every time popup changes
    }
  }, [info])


  return (
        <Popup
          ref={scrollRef}
          className='map-location-popup'
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
          <div style={{width: '100%', height: '1px', backgroundColor: 'black', margin: '7px 0px'}}></div>
          <div className='article-list-container'>
            {info.articles.length > 0 && info.articles.map((article, idx) => (
              <a style={{textDecoration: 'none'}}  href={article.webUrl} target="_blank">
                <div className='article-container' key={idx} style={{'marginBottom': '5px'}}>
                  <div>
                    <span>{idx+1}. {article.webTitle}</span>
                  </div>
                  <img id='article-image' width='150px'src={article.thumbnail}></img>
                </div>
              </a>
            ))}
          </div>
        </Popup>
  );
};

export default LocationPopup;
