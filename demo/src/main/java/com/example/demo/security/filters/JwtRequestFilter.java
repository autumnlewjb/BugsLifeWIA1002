package com.example.demo.security.filters;

import com.example.demo.security.MyUserDetailsService;
import com.example.demo.security.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final MyUserDetailsService myUserDetailsService;
    private final JwtUtil jwtUtil;
    private final Logger logger = LoggerFactory.getLogger(JwtException.class);

    @Value("${demo.jwtCookieName}")
    private String jwtCookieName;

    @Autowired
    public JwtRequestFilter(MyUserDetailsService myUserDetailsService, JwtUtil jwtUtil) {
        this.myUserDetailsService = myUserDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpRequest, HttpServletResponse httpResponse, FilterChain filterChain) throws ServletException, IOException {
        String jwt = null;
        Cookie[] cookies = httpRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(jwtCookieName)) {
                    jwt = cookie.getValue();
                }
            }
        }
        try {
            if (jwtUtil.validateToken(jwt)) {
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = myUserDetailsService.loadUserByUsername(jwtUtil.extractUsername(jwt));
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                            = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
                if (httpRequest.getRequestURI().equals("/api/login")) {
                    //Return authenticated user to homepage when they request for login page
                    httpResponse.sendRedirect("/api/project-dashboard");
                }
            }
        } catch (JwtException | IllegalArgumentException e) {
            logger.error(e.getMessage());
            httpRequest.setAttribute("exception", "Invalid JWT Token. Please login again.");
        }

        filterChain.doFilter(httpRequest, httpResponse);
    }
}
