package validator;

public enum ValidationResult {
  SUCCESS("Success"),
  INPUT_EMPTY_OR_NULL("Input is empty or null"),
  FROM_DATE_INVALID("fromDate is invalid or not in the format YYYY-MM-DD"),
  TO_DATE_INVALID("toDate is invalid or not in the format YYYY-MM-DD"),

  DATES_PRESENT("Both dates present"),
  DATES_NOT_PRESENT("Either one or both dates are missing"),
  DATES_INCONSISTENT("Either both dates must be present or neither should be");

  private final String message;

  ValidationResult(String message){
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
