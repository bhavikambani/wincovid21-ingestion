package com.wincovid21.ingestion.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum VerificationTypeEntity {

    VERIFIED("VERIFIED"),
    UNVERIFIED("UNVERIFIED");

    private String type;

}
