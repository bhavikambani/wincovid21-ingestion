package com.wincovid21.ingestion.exception;

public class UnAuthorizedUserException extends Exception {
    public UnAuthorizedUserException(String message) {
        super(message);
    }

}
