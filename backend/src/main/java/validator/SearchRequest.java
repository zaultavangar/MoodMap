package validator;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class SearchRequest{
  private final Optional<String> input;
  private final Optional<String> fromDate;
  private final Optional<String> toDate;
};

