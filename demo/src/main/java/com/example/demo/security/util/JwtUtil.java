/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.security.util;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.function.Function;

@Service
public class JwtUtil {

    @Value("${demo.secretKey}")
    private String secretKey;

    @Value("${demo.jwtExpirationInMs}")
    private Integer JWT_EXPIRATION_MS;

    private final Logger logger = LoggerFactory.getLogger(JwtException.class);

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder().setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_MS))
                .signWith(SignatureAlgorithm.HS256, secretKey).compact();
    }

    public Boolean validateToken(String token){
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);

        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
            logger.error(ex.getMessage());

        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
            logger.error(ex.getMessage());

        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
            logger.error(ex.getMessage());

        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
            logger.error(ex.getMessage());

        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty.");
            logger.error(ex.getMessage());
        }
        return true;
    }

}
