package com.wincovid21.ingestion.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class IngestionResponse<T> {
    private final HttpStatus httpStatus;
    private final T data;
    private final String transactionReference;
}
