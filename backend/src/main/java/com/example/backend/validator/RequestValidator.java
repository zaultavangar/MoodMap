package com.example.backend.validator;

import java.util.Optional;
import java.util.function.Function;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public interface RequestValidator extends Function<SearchRequest, ValidationResult> {
  static RequestValidator isInputValid(boolean required){
    return searchRequest -> {
      Optional<String> input = searchRequest.input();
      return !required || (input.isPresent() && !input.get().trim().isEmpty()) ?
          ValidationResult.SUCCESS : ValidationResult.INPUT_EMPTY_OR_NULL;
    };
  }


  static RequestValidator isDateRangeValid(boolean required) {
    return searchRequest -> {
      Optional<String> fromDateStr = searchRequest.fromDate();
      Optional<String> toDateStr = searchRequest.toDate();

      // Check if fromDate is empty
      if (fromDateStr.isEmpty()) {
        return required ? ValidationResult.FROM_DATE_INVALID : ValidationResult.SUCCESS;
      }

      // Check if toDate is empty
      if (toDateStr.isEmpty()) {
        return required ? ValidationResult.TO_DATE_INVALID : ValidationResult.SUCCESS;
      }

      // Validate fromDate format
      if (!fromDateStr.get().matches("^\\d{4}-\\d{2}-\\d{2}$")) {
        return ValidationResult.FROM_DATE_INVALID;
      }

      // Validate toDate format
      if (!toDateStr.get().matches("^\\d{4}-\\d{2}-\\d{2}$")) {
        return ValidationResult.TO_DATE_INVALID;
      }

      try {
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd");
        LocalDate fromDate = LocalDate.parse(fromDateStr.get(), format);
        LocalDate toDate = LocalDate.parse(toDateStr.get(), format);

        // Check if fromDate is after toDate
        if (fromDate.isAfter(toDate)) {
          return ValidationResult.DATE_RANGE_INVALID;
        }

        return ValidationResult.SUCCESS;
      } catch (Exception e) {
        return ValidationResult.DATE_RANGE_INVALID;
      }
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

  default RequestValidator and(RequestValidator other){
    return searchRequest -> {
      ValidationResult result = this.apply(searchRequest);
      return result.equals(ValidationResult.SUCCESS) ? other.apply(searchRequest) : result;
    };
  }

}
