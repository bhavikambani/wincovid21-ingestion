package com.wincovid21.ingestion.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum VerificationType {

    VERIFIED("Verified"),
    UNVERIFIED("Unverified");

    private String value;

}
