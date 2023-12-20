package com.example.backend.validator;

import lombok.Getter;

/**
 * Enumeration of possible validation results with associated messages.
 */
@Getter
public enum ValidationResult {
  SUCCESS("Success"),
  INPUT_EMPTY_OR_NULL("Input is empty or null"),
  FROM_DATE_INVALID("fromDate is invalid or not in the format yyyy-MM-dd"),
  TO_DATE_INVALID("toDate is invalid or not in the format YYYY-MM-DD"),
  DATE_RANGE_INVALID("Dates must be in yyyy-MM-dd format and fromDate must be less than toDate"),
  DATES_PRESENT("Both dates present"),
  DATES_NOT_PRESENT("Either one or both dates are missing"),
  DATES_INCONSISTENT("Either both dates must be present or neither should be");

  private final String message;

  /**
   * Constructs a ValidationResult with a specific message.
   *
   * @param message The message associated with the validation result.
   */
  ValidationResult(String message){
    this.message = message;
  }

}
