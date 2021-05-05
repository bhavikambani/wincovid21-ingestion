package com.wincovid21.ingestion.util.monit;

public enum ProfileResponseType {

    TOTAL("total"),
    SUCCESS("success"),
    ERROR("error"),
    BAD_REQUEST("bad-request"),
    TIMER("timer");

    private String statusProfile;

    ProfileResponseType(String statusProfile) {
        this.statusProfile = statusProfile;
    }

    public String getValue() {
        return statusProfile;
    }

}
