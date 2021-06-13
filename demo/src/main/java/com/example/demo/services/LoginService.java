package com.example.demo.services;

import com.example.demo.models.User;
import com.example.demo.security.MyUserDetailsService;
import com.example.demo.security.models.AuthenticateRequest;
import com.example.demo.security.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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

    @Value("${demo.jwtCookieName}")
    private String jwtCookieName;

    @Autowired
    public LoginService(AuthenticationManager authenticationManager, MyUserDetailsService myUserDetailsService, JwtUtil jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.myUserDetailsService = myUserDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    public ResponseEntity<User> logIn(AuthenticateRequest authenticateRequest) {
        final User user = myUserDetailsService.getUser(authenticateRequest.getUsername());
        final UserDetails userDetails = myUserDetailsService.loadUserByUsername(authenticateRequest.getUsername());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,
                        createAccessTokenCookie(jwtTokenUtil.generateToken(userDetails)).toString())
                .body(user);
    }

    public ResponseCookie createAccessTokenCookie(String token) {
        return ResponseCookie.from(jwtCookieName, token)
                .maxAge(-1) // the cookie is removed when the browser is closed
                // .domain("localhost")
                .path("/")
                .httpOnly(true)
                .build();
    }
}
