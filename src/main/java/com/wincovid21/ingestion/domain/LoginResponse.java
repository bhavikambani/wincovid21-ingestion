package com.wincovid21.ingestion.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private final String user;
    private final String name;
    private final String token;
    private final String message;
}
