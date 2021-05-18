/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.security.util;

import com.example.demo.models.User;
import io.jsonwebtoken.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtUtil {
    private String secretKey="secret";

    @Value("${demo.jwtExpirationInMs}")
    private Integer JWT_EXPIRATION_MS;

    @Value("${demo.refreshTokenExpirationInMs}")
    private Integer REFRESH_EXPIRATION_MS;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver) {
        final Claims claims=extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }
    
    public String generateToken(UserDetails userDetails) {
        Map<String,Object> claims=new HashMap<>();
        return Jwts.builder().setClaims(claims).setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+JWT_EXPIRATION_MS))
                .signWith(SignatureAlgorithm.HS256, secretKey).compact();
    }

    public String generateRefreshToken(User user) {
        Map<String,Object> claims=new HashMap<>();
        return Jwts.builder().setClaims(claims).setSubject(user.getUser_id().toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_MS))
                .signWith(SignatureAlgorithm.HS256, secretKey).compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails){
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return extractUsername(token).equals(userDetails.getUsername());
        } catch (SignatureException ex) {
            System.out.println("Incorrect signature");

        } catch (MalformedJwtException ex) {
            System.out.println("Malformed jwt token");

        } catch (ExpiredJwtException ex) {
            System.out.println("Token expired. Refresh required");

        } catch (UnsupportedJwtException ex) {
            System.out.println("Unsupported JWT token");

        } catch (IllegalArgumentException ex) {
            System.out.println("Illegal argument token");
        }
        return false;
    }

}
