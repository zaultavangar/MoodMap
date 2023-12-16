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
} from "@mui/material";
import NewspaperIcon from "@mui/icons-material/Newspaper";
import CircleIcon from '@mui/icons-material/Circle';
import { useRecoilValue } from "recoil";
import { searchByDateRangeState, searchQueryState, searchResultsState, selectedMonthState, selectedYearState  } from "~/atoms";
import { useSearch } from "~/hooks/useSearch";
import * as d3 from "d3";

const SearchResults = () => {

  const searchQuery = useRecoilValue(searchQueryState);
  const searchResults = useRecoilValue(searchResultsState);
  const selectedMonth = useRecoilValue(selectedMonthState);
  const selectedYear = useRecoilValue(selectedYearState);
  const searchByDateRange = useRecoilValue(searchByDateRangeState);

  const {search} = useSearch();

  useEffect(() => {
    search(searchQuery);
  }, [searchQuery]);

  useEffect(() => {
    if (searchQuery.length > 0){
      search(searchQuery);
    }
  }, [selectedMonth, selectedYear, searchByDateRange])

  const interpolater = d3.interpolateRgbBasis(["red", "yellow", "green"]);

  return (
    <Paper elevation={2} style={{maxHeight: '80vh', overflow: 'scroll'}}>
      <List dense={false}>
        {searchResults.map((result, index) => (
          <Link href={result.webUrl} target="_blank" underline="none" color={'inherit'}>
            <ListItemButton key={index}>
              <ListItemIcon>
                <NewspaperIcon />
              </ListItemIcon>
                <Stack
                  alignItems={"center"}
                  direction="row"
                  spacing={2}
                >
                  <ListItemText 
                    primary={result.webTitle} 
                    secondary={
                      <Stack
                        direction='column'
                        justifyContent={'center'}
                        spacing={1}
                        >
                        {result.webPublicationDate.substring(0,10)} 
                      </Stack> 
                    }/>
                  <CircleIcon style={{color: interpolater(result.sentimentScore), fontSize: '20px'}}/>
                  <img src={result.thumbnail} alt='Article image' width='100px' style={{borderRadius: '5px'}}/>
                </Stack>
            </ListItemButton>
          </Link>
        ))}
      </List>
    </Paper>
  );
};

export default SearchResults;
