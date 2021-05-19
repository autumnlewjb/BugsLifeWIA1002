package com.example.demo.security.models;

public class AuthenticateResponse {
    private final String user;

    public AuthenticateResponse(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }
}
