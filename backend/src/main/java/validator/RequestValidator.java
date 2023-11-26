package validator;

import static validator.ValidationResult.*;

import java.util.Optional;
import java.util.function.Function;

public interface RequestValidator extends Function<SearchRequest, ValidationResult> {
  static RequestValidator isInputValid(boolean required){
    return searchRequest -> {
      Optional<String> input = searchRequest.getInput();
      return !required || (input.isPresent() && !input.get().trim().isEmpty()) ?
          SUCCESS : INPUT_EMPTY_OR_NULL;
    };
  }

  static RequestValidator areDatesPresent() {
    return searchRequest -> {
      Optional<String> fromDate = searchRequest.getFromDate();
      Optional<String> toDate = searchRequest.getToDate();
      boolean fromDatePresent = fromDate.isPresent();
      boolean toDatePresent = toDate.isPresent();
      if (fromDatePresent && toDatePresent) {
        return DATES_PRESENT;
      } else if (!fromDatePresent && !toDatePresent) {
        return DATES_NOT_PRESENT;
      } else {
        return DATES_INCONSISTENT;
      }
    };
  }

  static RequestValidator isFromDateValid(boolean required){
    return searchRequest -> {
      Optional<String> fromDate = searchRequest.getFromDate();
      if (fromDate.isPresent()){
        // should be in yyyy-mm-dd format
        return fromDate.get().matches("^\\d{4}-\\d{2}-\\d{2}$") ?
          SUCCESS : FROM_DATE_INVALID;
      }
      return !required ? SUCCESS : FROM_DATE_INVALID;
    };
  }

  static RequestValidator isToDateValid(boolean required){
    return searchRequest -> {
      Optional<String> toDate = searchRequest.getToDate();
      if (toDate.isPresent()){
        // should be in yyyy-mm-dd format
        return toDate.get().matches("^\\d{4}-\\d{2}-\\d{2}$") ?
            SUCCESS : TO_DATE_INVALID;
      }
      return !required ? SUCCESS : TO_DATE_INVALID;
    };
  }

  default RequestValidator and(RequestValidator other){
    return searchRequest -> {
      ValidationResult result = this.apply(searchRequest);
      return result.equals(SUCCESS) ? other.apply(searchRequest) : result;
    };
  }

}
