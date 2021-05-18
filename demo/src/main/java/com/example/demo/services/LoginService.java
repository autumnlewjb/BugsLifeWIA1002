package com.example.demo.services;

import com.example.demo.models.User;
import com.example.demo.security.MyUserDetailsService;
import com.example.demo.security.models.AuthenticateRequest;
import com.example.demo.security.models.AuthenticateResponse;
import com.example.demo.security.util.CookieUtil;
import com.example.demo.security.util.JwtUtil;
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

    @Autowired
    public LoginService(AuthenticationManager authenticationManager, MyUserDetailsService myUserDetailsService, JwtUtil jwtTokenUtil, CookieUtil cookieUtil) {
        this.authenticationManager = authenticationManager;
        this.myUserDetailsService = myUserDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.cookieUtil = cookieUtil;
    }

    public ResponseEntity<AuthenticateResponse> logIn(String refreshToken, AuthenticateRequest authenticateRequest){
        authenticate(authenticateRequest.getUsername(), authenticateRequest.getPassword());
        final User user = myUserDetailsService.getUser(authenticateRequest.getUsername());
        final UserDetails userDetails = myUserDetailsService.loadUserByUsername(authenticateRequest.getUsername());
        final String jwt = jwtTokenUtil.generateToken(userDetails);
        if (refreshToken == null || !jwtTokenUtil.validateToken(refreshToken, userDetails)) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE,
                            cookieUtil.createRefreshTokenCookie(jwtTokenUtil.generateRefreshToken(user)).toString())
                    .body(new AuthenticateResponse(jwt));
        }
        return ResponseEntity.ok(new AuthenticateResponse(jwt));
    }

    private void authenticate(String username, String password){
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

    public ResponseEntity<AuthenticateResponse> refreshJwtToken(String refreshToken){
        UserDetails userDetails = myUserDetailsService.loadUserByUsername(jwtTokenUtil.extractUsername(refreshToken));
        if (!jwtTokenUtil.validateToken(refreshToken, userDetails)) {
            String jwt = jwtTokenUtil.generateToken(userDetails);
            return ResponseEntity.ok(new AuthenticateResponse(jwt));
        }
        throw new RuntimeException("An error occurred.Please login again");
    }

    /*public boolean authenticate(String username, String password) {
        User user = userService.getUser(username);
        if (user == null) {
            return false;
        }
        if (!user.getPassword().equals(password)) {
            return false;
        }

        return true;
    }*/
}
