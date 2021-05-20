package com.example.demo.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException {
        String exception = (String) httpServletRequest.getAttribute("exception");
        if (exception != null) {
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, exception);
        } else {
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
    }
}
