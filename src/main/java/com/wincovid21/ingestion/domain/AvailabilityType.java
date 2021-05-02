package com.wincovid21.ingestion.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AvailabilityType {

    OUT_OF_STOCK("Out of Stock"),
    AVAILABLE("Available");

    private String value;
}
