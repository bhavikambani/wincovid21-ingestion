package com.covimyn.ingestion.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum VerificationType {

    VERIFIED_AND_DONOR_AVAILABLE("Verified and Donor Available"),
    VERIFIED_AND_STOCK_AVAILABLE("Verified and Stock Available"),
    VERIFIED_BUT_DONOR_UNAVAILBALE("Verified but Donor Unavailable"),
    VERIFIED_BUT_STOCK_UNAVIALABLE("Verified but Stock Unavailable");

    private String value;

}
