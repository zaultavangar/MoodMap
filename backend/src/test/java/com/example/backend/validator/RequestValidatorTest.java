package com.example.backend.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RequestValidatorTest {

  @Test
  void testIsInputValid_RequiredAndPresent() {
    SearchRequest request = new SearchRequest(
        Optional.of("input"),
        Optional.empty(),
        Optional.empty());
    ValidationResult result = RequestValidator.isInputValid(true).apply(request);
    assertEquals(ValidationResult.SUCCESS, result);
  }

  @Test
  void testIsInputValid_RequiredAndAbsent() {
    SearchRequest request = new SearchRequest(
        Optional.empty(),
        Optional.empty(),
        Optional.empty());
    ValidationResult result = RequestValidator.isInputValid(true).apply(request);
    assertEquals(ValidationResult.INPUT_EMPTY_OR_NULL, result);
  }

  @Test
  void testIsInputValid_NotRequiredAndAbsent() {
    SearchRequest request = new SearchRequest(
        Optional.empty(),
        Optional.empty(),
        Optional.empty());
    ValidationResult result = RequestValidator.isInputValid(false).apply(request);
    assertEquals(ValidationResult.SUCCESS, result);
  }

  @Test
  void testIsDateRangeValid_RequiredAndPresentAndValid(){
    SearchRequest request = new SearchRequest(
        Optional.of("input"),
        Optional.of("2023-01-01"),
        Optional.of("2023-01-02"));
    ValidationResult result = RequestValidator.isDateRangeValid(true).apply(request);
    assertEquals(ValidationResult.SUCCESS, result);
  }


  @Test
  void testIsDateRangeValid_RequiredAndFromDateAbsent(){
    SearchRequest request = new SearchRequest(
        Optional.of("input"),
        Optional.empty(),
        Optional.of("2023-01-02"));
    ValidationResult result = RequestValidator.isDateRangeValid(true).apply(request);
    assertEquals(ValidationResult.FROM_DATE_INVALID, result);
  }

  @Test
  void testIsDateRangeValid_RequiredAndToDateAbsent(){
    SearchRequest request = new SearchRequest(
        Optional.of("input"),
        Optional.of("2023-01-02"),
        Optional.empty());
    ValidationResult result = RequestValidator.isDateRangeValid(true).apply(request);
    assertEquals(ValidationResult.TO_DATE_INVALID, result);
  }

  @Test
  void testIsDateRangeValid_RequiredAndFromDateInvalidFormat(){
    SearchRequest request = new SearchRequest(
        Optional.empty(),
        Optional.of("2023-1-02"), // invalid format
        Optional.of("2023-1-20"));
    ValidationResult result = RequestValidator.isDateRangeValid(true).apply(request);
    assertEquals(ValidationResult.FROM_DATE_INVALID, result);
  }

  @Test
  void testIsDateRangeValid_RequiredAndToDateInvalidFormat(){
    SearchRequest request = new SearchRequest(
        Optional.of("input"),
        Optional.of("2023-01-02"),
        Optional.of("01-02-2019"));  // invalid format
    ValidationResult result = RequestValidator.isDateRangeValid(true).apply(request);
    assertEquals(ValidationResult.TO_DATE_INVALID, result);
  }

  @Test
  void testIsDateRangeValid_RequiredAndFromDateAfterToDate(){
    SearchRequest request = new SearchRequest(
        Optional.empty(),
        Optional.of("2023-05-02"),
        Optional.of("2023-01-02")); // before fromDate
    ValidationResult result = RequestValidator.isDateRangeValid(true).apply(request);
    assertEquals(ValidationResult.DATE_RANGE_INVALID, result);
  }

  @Test
  void testIsDateRangeValid_NotRequiredAndDatesAbsentOrInvalid(){
    SearchRequest request = new SearchRequest(
        Optional.of("input"),
        Optional.empty(), // empty
        Optional.of("20-01-02")); // invalid format
    ValidationResult result = RequestValidator.isDateRangeValid(false).apply(request);
    assertEquals(ValidationResult.SUCCESS, result);
  }

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
