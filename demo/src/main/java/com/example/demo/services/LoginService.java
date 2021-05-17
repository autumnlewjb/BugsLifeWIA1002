package com.example.demo.services;

import com.example.demo.models.RefreshToken;
import com.example.demo.models.User;
import com.example.demo.security.MyUserDetailsService;
import com.example.demo.security.models.AuthenticateRequest;
import com.example.demo.security.models.AuthenticateResponse;
import com.example.demo.security.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class LoginService {
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private MyUserDetailsService myUserDetailsService;
    @Autowired
    private JwtUtil jwtTokenUtil;
    @Autowired
    private RefreshTokenService refreshTokenService;

    public ResponseEntity<?> authenticate(AuthenticateRequest authenticateRequest) throws Exception {
        authenticate(authenticateRequest.getUsername(), authenticateRequest.getPassword());
        final User user = myUserDetailsService.getUser(authenticateRequest.getUsername());
        final UserDetails userDetails = myUserDetailsService.loadUserByUsername(authenticateRequest.getUsername());
        final String jwt = jwtTokenUtil.generateToken(userDetails);
        RefreshToken refreshToken;
        if(refreshTokenService.findById(user.getUser_id()).isPresent()){
            refreshToken = refreshTokenService.findById(user.getUser_id()).get();
            if (jwtTokenUtil.isTokenExpired(refreshToken.getToken())) {
                refreshTokenService.delete(refreshToken);
                refreshToken = new RefreshToken(user.getUser_id(), jwtTokenUtil.generateRefreshToken(user));
                refreshTokenService.save(refreshToken);
            }
            return ResponseEntity.ok(new AuthenticateResponse(jwt, refreshToken.getToken()));
        }
        refreshToken = new RefreshToken(user.getUser_id(), jwtTokenUtil.generateRefreshToken(user));
        refreshTokenService.save(refreshToken);
        return ResponseEntity.ok(new AuthenticateResponse(jwt, refreshToken.getToken()));
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        } catch (Exception e) {
            throw new Exception(e.toString());
        }
    }

    public Optional<String> refreshJwtToken(RefreshToken refreshToken) throws Exception {
        return Optional.of(refreshTokenService.findByToken(refreshToken.getToken())
                .map(token -> {
                    jwtTokenUtil.isTokenExpired(token.getToken());
                    return token;
                })
                .map(RefreshToken::getId)
                .map(userService::getUserById)
                .map(User::getUsername)
                .map(myUserDetailsService::loadUserByUsername)
                .map(jwtTokenUtil::generateToken))
                .orElseThrow(() -> new Exception("Missing refresh token in database.Please login again"));
    }

    public void logoutUser(String refreshToken) {
        refreshTokenService.findByToken(refreshToken)
                .ifPresent(refreshTokenService::delete);
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
