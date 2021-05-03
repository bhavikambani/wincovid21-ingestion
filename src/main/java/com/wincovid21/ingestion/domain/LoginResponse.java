package com.wincovid21.ingestion.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String user;
    private String token;
    private String message;
}
