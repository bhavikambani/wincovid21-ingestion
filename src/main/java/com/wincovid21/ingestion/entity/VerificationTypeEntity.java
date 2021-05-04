package com.wincovid21.ingestion.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum VerificationTypeEntity {

    VERIFIED("VERIFIED"),
    NOCHANGE("NOCHANGE"),
    UNVERIFIED("UNVERIFIED");

    private String type;

}
