import {
  List,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Paper,
} from "@mui/material";
import NewspaperIcon from "@mui/icons-material/Newspaper";

const SearchResults = ({ results }: { results: string[] }) => {
  return (
    <Paper elevation={2}>
      <List dense={false}>
        {results.map((result, index) => (
          <ListItemButton key={index}>
            <ListItemIcon>
              <NewspaperIcon />
            </ListItemIcon>
            <ListItemText primary={result} />
          </ListItemButton>
        ))}
      </List>
    </Paper>
  );
};

export default SearchResults;
