package com.example.backend.validator;

import java.util.Optional;
import java.util.function.Function;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

// STATUS: Not tested
public interface RequestValidator extends Function<SearchRequest, ValidationResult> {
  static RequestValidator isInputValid(boolean required){
    return searchRequest -> {
      Optional<String> input = searchRequest.input();
      return !required || (input.isPresent() && !input.get().trim().isEmpty()) ?
          ValidationResult.SUCCESS : ValidationResult.INPUT_EMPTY_OR_NULL;
    };
  }


  static  RequestValidator isDateRangeValid(boolean required){
    return searchRequest -> {
      Optional<String> fromDateStr = searchRequest.fromDate();
      Optional<String> toDateStr = searchRequest.toDate();
      if (fromDateStr.isEmpty()) {
        return required ? ValidationResult.FROM_DATE_INVALID : ValidationResult.SUCCESS;
      }
      if (toDateStr.isEmpty()) {
        return required ? ValidationResult.FROM_DATE_INVALID : ValidationResult.SUCCESS;
      }
      try {
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd");
        LocalDate fromDate = LocalDate.parse(fromDateStr.get(), format);
        LocalDate toDate = LocalDate.parse(toDateStr.get(), format);
        return fromDate.isAfter(toDate) ? ValidationResult.DATE_RANGE_INVALID : ValidationResult.SUCCESS;

      } catch (Exception e){
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

  static RequestValidator isFromDateValid(boolean required) {
    return searchRequest -> {
      Optional<String> fromDate = searchRequest.fromDate();
      if (fromDate.isEmpty()) {
        return required ? ValidationResult.FROM_DATE_INVALID : ValidationResult.SUCCESS;
      }

      String dateStr = fromDate.get();
      if (!dateStr.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
        return ValidationResult.FROM_DATE_INVALID;
      }

      try {
        String[] parts = dateStr.split("-");
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);

        if (month < 1 || month > 12 || day < 1 || day > 31) {
          return ValidationResult.FROM_DATE_INVALID;
        }

        return ValidationResult.SUCCESS;
      } catch (NumberFormatException e) {
        return ValidationResult.FROM_DATE_INVALID;
      }
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
