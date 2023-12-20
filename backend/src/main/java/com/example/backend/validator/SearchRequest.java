package com.example.backend.validator;

import java.util.Optional;
import lombok.Data;

/**
 * Represents a search request with optional input, fromDate, and toDate fields.
 */
public record SearchRequest(Optional<String> input, Optional<String> fromDate,
                            Optional<String> toDate) {

};

