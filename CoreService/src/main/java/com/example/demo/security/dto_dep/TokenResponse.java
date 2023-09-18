package com.example.demo.security.dto_dep;


import lombok.Data;

@Data
public class TokenResponse {

    private String message;

    public TokenResponse(String message) {
        this.message = message;
    }
}
