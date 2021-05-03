package com.wincovid21.ingestion.domain;

import lombok.Data;

@Data
public class LoginRequest {
    private String user;
    private String password;
}
