package com.example.demo.services;

import com.example.demo.models.User;
import com.example.demo.security.MyUserDetailsService;
import com.example.demo.security.models.AuthenticateRequest;
import com.example.demo.security.models.AuthenticateResponse;
import com.example.demo.security.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final MyUserDetailsService myUserDetailsService;
    private final JwtUtil jwtTokenUtil;
    private final ObjectMapper objectMapper;

    @Value("${demo.jwtCookieName}")
    private String jwtCookieName;

    @Autowired
    public LoginService(AuthenticationManager authenticationManager, MyUserDetailsService myUserDetailsService,
                        JwtUtil jwtTokenUtil, ObjectMapper objectMapper) {
        this.authenticationManager = authenticationManager;
        this.myUserDetailsService = myUserDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.objectMapper = objectMapper;
    }

    public ResponseEntity<AuthenticateResponse> logIn(AuthenticateRequest authenticateRequest) {
        authenticate(authenticateRequest.getUsername(), authenticateRequest.getPassword());
        final User user = myUserDetailsService.getUser(authenticateRequest.getUsername());
        final UserDetails userDetails = myUserDetailsService.loadUserByUsername(authenticateRequest.getUsername());
        String userString = "";
        try {
            userString = objectMapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,
                        createAccessTokenCookie(jwtTokenUtil.generateToken(userDetails)).toString())
                .body(new AuthenticateResponse(userString));
    }

    private void authenticate(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new RuntimeException("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new RuntimeException("INVALID_CREDENTIALS", e);
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
    }

    public ResponseCookie createAccessTokenCookie(String token) {
        return ResponseCookie.from(jwtCookieName, token)
                .maxAge(-1) // the cookie is removed when the browser is closed
                .domain("localhost")
                .path("/")
                .httpOnly(true)
                .sameSite("Lax")
                .build();
    }
}
