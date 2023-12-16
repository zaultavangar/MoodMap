package com.example.backend.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RequestValidatorTest {
  /**
   * Validates that a required input is correctly identified as valid when present.
   * Scenario: The input is provided and marked as required.
   * Expected Outcome: The validation result should indicate success, represented by ValidationResult.SUCCESS.
   */
  @Test
  void testIsInputValid_RequiredAndPresent() {
    SearchRequest request = new SearchRequest(
        Optional.of("input"),
        Optional.empty(),
        Optional.empty());
    ValidationResult result = RequestValidator.isInputValid(true).apply(request);
    assertEquals(ValidationResult.SUCCESS, result);
  }

  /**
   * Checks the validator's response when a required input is absent.
   * Scenario: An input is required but not provided in the request.
   * Expected Outcome: The validation should fail, returning ValidationResult.INPUT_EMPTY_OR_NULL.
   */
  @Test
  void testIsInputValid_RequiredAndAbsent() {
    SearchRequest request = new SearchRequest(
        Optional.empty(),
        Optional.empty(),
        Optional.empty());
    ValidationResult result = RequestValidator.isInputValid(true).apply(request);
    assertEquals(ValidationResult.INPUT_EMPTY_OR_NULL, result);
  }

  /**
   * Ensures optional inputs are correctly handled by the validator.
   * Scenario: An input is not provided and not required.
   * Expected Outcome: Validation should succeed, indicated by ValidationResult.SUCCESS.
   */
  @Test
  void testIsInputValid_NotRequiredAndAbsent() {
    SearchRequest request = new SearchRequest(
        Optional.empty(),
        Optional.empty(),
        Optional.empty());
    ValidationResult result = RequestValidator.isInputValid(false).apply(request);
    assertEquals(ValidationResult.SUCCESS, result);
  }

  /**
   * Validates a date range that is both required and provided in a valid format.
   * Scenario: A valid date range is given in the request and marked as required.
   * Expected Outcome: The validation should pass, returning ValidationResult.SUCCESS.
   */
  @Test
  void testIsDateRangeValid_RequiredAndPresentAndValid(){
    SearchRequest request = new SearchRequest(
        Optional.of("input"),
        Optional.of("2023-01-01"),
        Optional.of("2023-01-02"));
    ValidationResult result = RequestValidator.isDateRangeValid(true).apply(request);
    assertEquals(ValidationResult.SUCCESS, result);
  }


  /**
   * Tests the validation response when the 'from' date is missing but required.
   * Scenario: The 'from' date in the date range is absent in a request where it's required.
   * Expected Outcome: The validation should fail, returning ValidationResult.FROM_DATE_INVALID.
   */
  @Test
  void testIsDateRangeValid_RequiredAndFromDateAbsent(){
    SearchRequest request = new SearchRequest(
        Optional.of("input"),
        Optional.empty(),
        Optional.of("2023-01-02"));
    ValidationResult result = RequestValidator.isDateRangeValid(true).apply(request);
    assertEquals(ValidationResult.FROM_DATE_INVALID, result);
  }

  /**
   * Verifies the validation when the 'to' date is absent in a required date range.
   * Scenario: The 'to' date is not provided in the request, but the date range is required.
   * Expected Outcome: Results in a validation failure, indicated by ValidationResult.TO_DATE_INVALID.
   */
  @Test
  void testIsDateRangeValid_RequiredAndToDateAbsent(){
    SearchRequest request = new SearchRequest(
        Optional.of("input"),
        Optional.of("2023-01-02"),
        Optional.empty());
    ValidationResult result = RequestValidator.isDateRangeValid(true).apply(request);
    assertEquals(ValidationResult.TO_DATE_INVALID, result);
  }

  /**
   * Tests validation for a 'from' date in an invalid format within a required date range.
   * Scenario: The 'from' date is provided in an incorrect format in a required date range.
   * Expected Outcome: Validation fails, denoted by ValidationResult.FROM_DATE_INVALID.
   */
  @Test
  void testIsDateRangeValid_RequiredAndFromDateInvalidFormat(){
    SearchRequest request = new SearchRequest(
        Optional.empty(),
        Optional.of("2023-1-02"), // invalid format
        Optional.of("2023-1-20"));
    ValidationResult result = RequestValidator.isDateRangeValid(true).apply(request);
    assertEquals(ValidationResult.FROM_DATE_INVALID, result);
  }

  /**
   * Assesses the validation of an invalid 'to' date format within a required date range.
   * Scenario: The 'to' date is given in an incorrect format, and the date range is mandatory.
   * Expected Outcome: Validation results in a failure, returning ValidationResult.TO_DATE_INVALID.
   */
  @Test
  void testIsDateRangeValid_RequiredAndToDateInvalidFormat(){
    SearchRequest request = new SearchRequest(
        Optional.of("input"),
        Optional.of("2023-01-02"),
        Optional.of("01-02-2019"));  // invalid format
    ValidationResult result = RequestValidator.isDateRangeValid(true).apply(request);
    assertEquals(ValidationResult.TO_DATE_INVALID, result);
  }

  /**
   * Checks validation when the 'from' date is after the 'to' date in a required date range.
   * Scenario: An incorrect date range is provided where the 'from' date is later than the 'to' date.
   * Expected Outcome: Results in a ValidationResult.DATE_RANGE_INVALID due to the invalid date range.
   */
  @Test
  void testIsDateRangeValid_RequiredAndFromDateAfterToDate(){
    SearchRequest request = new SearchRequest(
        Optional.empty(),
        Optional.of("2023-05-02"),
        Optional.of("2023-01-02")); // before fromDate
    ValidationResult result = RequestValidator.isDateRangeValid(true).apply(request);
    assertEquals(ValidationResult.DATE_RANGE_INVALID, result);
  }

  /**
   * Verifies validation behavior for optional date ranges that are absent or invalid.
   * Scenario: The date range is optional, and either absent or in an invalid format.
   * Expected Outcome: The validation passes, indicated by ValidationResult.SUCCESS.
   */
  @Test
  void testIsDateRangeValid_NotRequiredAndDatesAbsentOrInvalid(){
    SearchRequest request = new SearchRequest(
        Optional.of("input"),
        Optional.empty(), // empty
        Optional.of("20-01-02")); // invalid format
    ValidationResult result = RequestValidator.isDateRangeValid(false).apply(request);
    assertEquals(ValidationResult.SUCCESS, result);
  }

  /**
   * Confirms that the presence of both dates in a request is correctly identified.
   * Scenario: Both 'from' and 'to' dates are provided in the request.
   * Expected Outcome: ValidationResult.DATES_PRESENT confirms the presence of both dates.
   */
  @Test
  void testAreDatesPresent_DatesPresent() {
    SearchRequest request = new SearchRequest(
        Optional.empty(),
        Optional.of("2023-01-01"),
        Optional.of("2023-05-01")
    );
    RequestValidator validator = RequestValidator.areDatesPresent();
    ValidationResult result = validator.apply(request);
    assertEquals(ValidationResult.DATES_PRESENT, result);
  }

  /**
   * Tests the validator's response when no dates are present in the request.
   * Scenario: Neither 'from' nor 'to' dates are provided.
   * Expected Outcome: The validation result is ValidationResult.DATES_NOT_PRESENT, indicating absence of dates.
   */
  @Test
  void testAreDatesPresent_NoDates() {
    SearchRequest request = new SearchRequest(
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    );
    RequestValidator validator = RequestValidator.areDatesPresent();
    ValidationResult result = validator.apply(request);
    assertEquals(ValidationResult.DATES_NOT_PRESENT, result);
  }

  /**
   * Evaluates validation when only the 'from' date is present in the request.
   * Scenario: The 'from' date is provided, but the 'to' date is absent.
   * Expected Outcome: ValidationResult.DATES_INCONSISTENT due to the incomplete date range.
   */
  @Test
  void testAreDatesPresent_OnlyFromDate() {
    SearchRequest request = new SearchRequest(
        Optional.empty(),
        Optional.of("2023-01-01"),
        Optional.empty()
    );
    RequestValidator validator = RequestValidator.areDatesPresent();
    ValidationResult result = validator.apply(request);
    assertEquals(ValidationResult.DATES_INCONSISTENT, result);
  }

  /**
   * Checks validation for cases where only the 'to' date is present.
   * Scenario: The 'to' date is provided without a corresponding 'from' date.
   * Expected Outcome: Results in ValidationResult.DATES_INCONSISTENT due to partial date information.
   */
  @Test
  void testAreDatesPresent_OnlyToDate() {
    SearchRequest request = new SearchRequest(
        Optional.empty(),
        Optional.empty(),
        Optional.of("2023-05-01")
    );
    RequestValidator validator = RequestValidator.areDatesPresent();
    ValidationResult result = validator.apply(request);
    assertEquals(ValidationResult.DATES_INCONSISTENT, result);
  }
}
