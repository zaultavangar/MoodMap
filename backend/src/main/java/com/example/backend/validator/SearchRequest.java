package com.example.backend.validator;

import java.util.Optional;
import lombok.Data;

public record SearchRequest(Optional<String> input, Optional<String> fromDate,
                            Optional<String> toDate) {

};

