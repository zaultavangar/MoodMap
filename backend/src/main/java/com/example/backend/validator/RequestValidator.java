package com.example.backend.validator;

import java.util.Optional;
import java.util.function.Function;

public interface RequestValidator extends Function<SearchRequest, ValidationResult> {
  static RequestValidator isInputValid(boolean required){
    return searchRequest -> {
      Optional<String> input = searchRequest.input();
      return !required || (input.isPresent() && !input.get().trim().isEmpty()) ?
          ValidationResult.SUCCESS : ValidationResult.INPUT_EMPTY_OR_NULL;
    };
  }

  static RequestValidator areDatesPresent() {
    return searchRequest -> {
      Optional<String> fromDate = searchRequest.fromDate();
      Optional<String> toDate = searchRequest.toDate();
      boolean fromDatePresent = fromDate.isPresent();
      boolean toDatePresent = toDate.isPresent();
      if (fromDatePresent && toDatePresent) {
        return ValidationResult.DATES_PRESENT;
      } else if (!fromDatePresent && !toDatePresent) {
        return ValidationResult.DATES_NOT_PRESENT;
      } else {
        return ValidationResult.DATES_INCONSISTENT;
      }
    };
  }

  static RequestValidator isFromDateValid(boolean required){
    return searchRequest -> {
      Optional<String> fromDate = searchRequest.fromDate();
      // should be in yyyy-mm-dd format
      return fromDate.map(s -> s.matches("^\\d{4}-\\d{2}-\\d{2}$") ?
          ValidationResult.SUCCESS : ValidationResult.FROM_DATE_INVALID).orElseGet(() -> !required ? ValidationResult.SUCCESS : ValidationResult.FROM_DATE_INVALID);
    };
  }

  static RequestValidator isToDateValid(boolean required){
    return searchRequest -> {
      Optional<String> toDate = searchRequest.toDate();
      // should be in yyyy-mm-dd format
      return toDate.map(s -> s.matches("^\\d{4}-\\d{2}-\\d{2}$") ?
          ValidationResult.SUCCESS : ValidationResult.TO_DATE_INVALID).orElseGet(() -> !required ? ValidationResult.SUCCESS : ValidationResult.TO_DATE_INVALID);
    };
  }

  default RequestValidator and(RequestValidator other){
    return searchRequest -> {
      ValidationResult result = this.apply(searchRequest);
      return result.equals(ValidationResult.SUCCESS) ? other.apply(searchRequest) : result;
    };
  }

}
