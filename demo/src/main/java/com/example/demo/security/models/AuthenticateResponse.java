package com.example.demo.security.models;

import com.example.demo.models.User;

public class AuthenticateResponse {
    private final String jwt;
    private final String user;

    public AuthenticateResponse(String jwt) {
        this.jwt = jwt;
        this.user = null;
    }

    public AuthenticateResponse(String jwt, String user) {
        this.jwt = jwt;
        this.user = user;
    }

    public String getJwt() {
        return jwt;
    }

    public String getUser() {
        return user;
    }
}
