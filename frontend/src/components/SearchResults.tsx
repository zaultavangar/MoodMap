import React, { useEffect } from "react";
import {
  Divider,
  Link,
  List,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Paper,
  Stack,
  Typography,
} from "@mui/material";
import NewspaperIcon from "@mui/icons-material/Newspaper";
import CircleIcon from "@mui/icons-material/Circle";
import { useRecoilState, useRecoilValue } from "recoil";
import NavigateBeforeIcon from '@mui/icons-material/NavigateBefore';
import NavigateNextIcon from '@mui/icons-material/NavigateNext';
import IconButton from '@mui/material/IconButton';
import {
  searchByDateRangeState,
  searchQueryState,
  searchResultsPageState,
  searchResultsState,
  selectedMonthState,
  selectedYearState,
} from "~/atoms";
import { useSearch } from "~/hooks/useSearch";
import * as d3 from "d3";

const SearchResults = () => {
  const searchQuery = useRecoilValue(searchQueryState);
  const searchResults = useRecoilValue(searchResultsState);
  const selectedMonth = useRecoilValue(selectedMonthState);
  const selectedYear = useRecoilValue(selectedYearState);
  const searchByDateRange = useRecoilValue(searchByDateRangeState);
  const [searchResultsPage, setSearchResultsPage] = useRecoilState(searchResultsPageState);

  const { search } = useSearch();

  useEffect(() => {
    setSearchResultsPage(0);
    search(searchQuery);
  }, [searchQuery]);

  useEffect(() => {
    if (searchQuery.length > 0) {
      setSearchResultsPage(0);
      search(searchQuery);
    }
  }, [selectedMonth, selectedYear, searchByDateRange]);

  const numPages = searchResults.length/10;

  const interpolater = d3.interpolateRgbBasis(["red", "yellow", "green"]);

  return (
    <Paper
      elevation={2}
      style={{ maxHeight: "80vh", overflow: "scroll" }}
      data-testid="search-results"
    >
      <List dense={false}>
        {searchResults.slice(searchResultsPage*10, (searchResultsPage*10)+10).map((result, index) => (
          <Link
            key={index}
            href={result.webUrl}
            target="_blank"
            underline="none"
            color={"inherit"}
          >
            <ListItemButton key={index}>
              <ListItemIcon>
                <NewspaperIcon />
              </ListItemIcon>
              <Stack alignItems={"center"} direction="row" spacing={2}>
                <ListItemText
                  primary={result.webTitle}
                  secondary={
                    <Typography 
                      variant="body2" 
                      component="div"
                      fontWeight={400}
                      color={'rgba(0, 0, 0, 0.6)'}
                      lineHeight={1.43}
                      >
                      {result.webPublicationDate.substring(0, 10)}
                    </Typography>
                  }
                />
                <CircleIcon
                  style={{
                    color: interpolater(result.sentimentScore),
                    fontSize: "20px",
                  }}
                />
                <img
                  src={result.thumbnail}
                  alt="Article image"
                  width="100px"
                  style={{ borderRadius: "5px" }}
                />
              </Stack>
            </ListItemButton>
          </Link>
        ))}
      </List>
      {searchResults.length>0 &&
        <Stack
        direction='row'
        justifyContent={'flex-end'}
        alignItems={'center'}
        spacing={1}
        padding={'10px'}
        >
        <IconButton
          disabled={searchResultsPage === 0}
          onClick={() => setSearchResultsPage(searchResultsPage-1)}
        >
          <NavigateBeforeIcon/>
        </IconButton>
        <Typography variant="body2" component="div">
          {searchResultsPage+1} of {Math.ceil(numPages)}
        </Typography>
        <IconButton
          disabled={searchResultsPage >= numPages-1}
          onClick={() => setSearchResultsPage(searchResultsPage+1)}
        >
          <NavigateNextIcon/>
        </IconButton>
      </Stack>
      
      }
      
    </Paper>
  );
};

export default SearchResults;
