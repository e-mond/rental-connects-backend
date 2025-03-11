package com.rentalconnects.backend.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component // Marks this class as a Spring component for dependency injection
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private final JwtUtil jwtUtil; // Utility class for handling JWT operations

    // Constructor-based dependency injection
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization"); // Get Authorization header
        System.out.println("Authorization Header: " + header); // Debug log
        String token = null;
        System.out.println("Extracted Token: " + token); // Debug log
        String username = null;
        System.out.println("Extracted Username: " + username); // Debug log

        // Extract token from the Authorization header if it starts with "Bearer "
        if (header != null && header.startsWith("Bearer ")) {
            System.out.println("Validating Token: " + jwtUtil.validateToken(token)); // Debug log
            token = header.substring(7);
            username = jwtUtil.extractUsername(token);
        }

        // Validate token and set authentication context if valid
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.validateToken(token)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        username, null, null);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continue filter chain
        filterChain.doFilter(request, response); 
    }
}
