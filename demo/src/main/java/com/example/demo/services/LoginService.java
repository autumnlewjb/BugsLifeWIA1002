package com.example.demo.services;

import com.example.demo.models.User;
import com.example.demo.security.MyUserDetailsService;
import com.example.demo.security.models.AuthenticateRequest;
import com.example.demo.security.models.AuthenticateResponse;
import com.example.demo.security.util.CookieUtil;
import com.example.demo.security.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Service
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final MyUserDetailsService myUserDetailsService;
    private final JwtUtil jwtTokenUtil;
    private final CookieUtil cookieUtil;
    private final ObjectMapper objectMapper;

    @Autowired
    public LoginService(AuthenticationManager authenticationManager, MyUserDetailsService myUserDetailsService,
                        JwtUtil jwtTokenUtil, CookieUtil cookieUtil, ObjectMapper objectMapper) {
        this.authenticationManager = authenticationManager;
        this.myUserDetailsService = myUserDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.cookieUtil = cookieUtil;
        this.objectMapper = objectMapper;
    }

    public ResponseEntity<AuthenticateResponse> logIn(String refreshToken, AuthenticateRequest authenticateRequest) {
        authenticate(authenticateRequest.getUsername(), authenticateRequest.getPassword());
        final User user = myUserDetailsService.getUser(authenticateRequest.getUsername());
        final UserDetails userDetails = myUserDetailsService.loadUserByUsername(authenticateRequest.getUsername());
        final String jwt = jwtTokenUtil.generateToken(userDetails);
        String userString = "";
        try{
            userString = objectMapper.writeValueAsString(user);
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }
        System.out.println(userString);
        if (refreshToken == null || !jwtTokenUtil.validateToken(refreshToken, userDetails)) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE,
                            cookieUtil.createRefreshTokenCookie(jwtTokenUtil.generateRefreshToken(user)).toString())
                    .body(new AuthenticateResponse(jwt, userString));
        }
        return ResponseEntity.ok(new AuthenticateResponse(jwt, userString));
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

    public ResponseEntity<AuthenticateResponse> refreshJwtToken(String refreshToken) {
        UserDetails userDetails = myUserDetailsService.loadUserByUsername(jwtTokenUtil.extractUsername(refreshToken));
        if (!jwtTokenUtil.validateToken(refreshToken, userDetails)) {
            String jwt = jwtTokenUtil.generateToken(userDetails);
            return ResponseEntity.ok(new AuthenticateResponse(jwt));
        }
        throw new RuntimeException("An error occurred.Please login again");
    }
}
