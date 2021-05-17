package com.example.demo.security.models;

public class AuthenticateResponse {
    private final String jwt;
    private final String refreshToken;

    public AuthenticateResponse(String jwt, String refreshToken) {
        this.jwt = jwt;
        this.refreshToken = refreshToken;
    }

    public String getJwt() {
        return jwt;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
