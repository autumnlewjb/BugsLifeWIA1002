package com.example.demo.security.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class CookieUtil {

    @Value("${demo.accessTokenName}")
    private String accessTokenName;

    @Value("${demo.refreshTokenName}")
    private String refreshTokenName;

    public ResponseCookie createAccessTokenCookie(String token) {
        return ResponseCookie.from(accessTokenName, token)
                .maxAge(-1) // the cookie is removed when the browser is closed
                .httpOnly(true)
                .domain("localhost")
                .path("/")
                .build();
    }

    public ResponseCookie createRefreshTokenCookie(String token) {
        return ResponseCookie.from(refreshTokenName, token)
                .maxAge(-1) // the cookie is removed when the browser is closed.
                .httpOnly(true)
                .domain("localhost")
                .path("/")
                .build();
    }

    public HttpCookie deleteAccessTokenCookie() {
        return ResponseCookie.from(accessTokenName, "").maxAge(0).httpOnly(true).path("/").build();
    }

    public HttpCookie deleteRefreshTokenCookie() {
        return ResponseCookie.from(refreshTokenName, "").maxAge(0).httpOnly(true).path("/").build();
    }
}
