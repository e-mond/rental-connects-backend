package com.rentalconnects.backend.security;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.rentalconnects.backend.service.JwtService;
import com.rentalconnects.backend.service.CustomUserDetailsService;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthorizationFilter.class);

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthorizationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        log.info("JwtAuthorizationFilter initialized");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getServletPath();
        String method = request.getMethod();
        log.debug("Processing request - Method: {}, Path: {}", method, path);

        // Skip JWT validation for public endpoints
        if (isPublicEndpoint(path, method)) {
            log.info("Skipping JWT authorization for public endpoint - Method: {}, Path: {}", method, path);
            filterChain.doFilter(request, response);
            return;
        }

        // Retrieve Authorization header
        String authHeader = request.getHeader("Authorization");
        log.debug("Authorization header: {}", authHeader != null ? authHeader : "null");

        // Check for valid Authorization header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("No valid Authorization header found for Path: {}. Header: {}", path, authHeader);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"No token provided or invalid format\"}");
            response.getWriter().flush();
            return;
        }

        // Extract and validate token
        String token = authHeader.substring(7);
        log.debug("Extracted token: {}", token.length() > 20 ? token.substring(0, 20) + "..." : token);

        String username;
        try {
            username = jwtService.extractEmail(token);
            if (username == null || username.isEmpty()) {
                log.warn("Username extracted from token is null or empty for Path: {}", path);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Invalid token: No username found\"}");
                response.getWriter().flush();
                return;
            }
            log.debug("Extracted username from token: {}", username);

            try {
                String role = jwtService.extractRole(token);
                Date expiration = jwtService.extractExpiration(token);
                log.debug("Token details - Role: {}, Expiration: {}", role, expiration);
            } catch (JwtException e) {
                log.error("JwtException while extracting token details: {}", e.getMessage());
            } catch (Exception e) {
                log.error("Unexpected error while extracting token details: {}", e.getMessage());
            }
        } catch (JwtException e) {
            log.warn("Invalid JWT token for Path: {}: {}", path, e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Invalid token: " + e.getMessage() + "\"}");
            response.getWriter().flush();
            return;
        } catch (IOException e) {
            log.error("Unexpected error during JWT validation for Path: {}: {}", path, e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Invalid token: Unexpected error\"}");
            response.getWriter().flush();
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                String roleFromToken = jwtService.extractRole(token);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + roleFromToken)));
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.debug("JWT token validated for user: {}, Authorities set: {}", username, Collections.singletonList("ROLE_" + roleFromToken));
            } catch (UsernameNotFoundException e) {
                log.error("User not found for username: {}: {}", username, e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"User not found\"}");
                response.getWriter().flush();
                return;
            } catch (JwtException e) {
                log.error("Invalid token while loading user details for username: {}: {}", username, e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Invalid token: " + e.getMessage() + "\"}");
                response.getWriter().flush();
                return;
            }

            if (jwtService.validateToken(token, username)) {
                log.debug("JWT token validated for user: {}", username);
            } else {
                boolean isExpired = jwtService.isTokenExpired(token);
                log.warn("JWT token validation failed for user: {}. Is token expired? {}", username, isExpired);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                String errorMessage = isExpired ? "Token expired" : "Token validation failed";
                response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"" + errorMessage + "\", \"isExpired\": " + isExpired + "}");
                response.getWriter().flush();
                return;
            }
        } else {
            log.debug("Authentication already set in SecurityContext for user: {}", username);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String path, String method) {
        boolean isPublic = (path.equals("/api/auth/signup") && method.equals("POST")) ||
                (path.equals("/api/auth/signin") && method.equals("POST")) ||
                (path.equals("/api/auth/login") && method.equals("POST")) ||
                (path.equals("/api/auth/test") && method.equals("GET")) ||
                (path.startsWith("/api/auth/") && method.equals("OPTIONS")) ||
                (path.startsWith("/images/")) ||
                (path.equals("/api/properties") && method.equals("GET")) ||
                (path.startsWith("/api/properties/") && method.equals("GET")) ||
                (path.startsWith("/uploads/properties/") && method.equals("GET")) ||
                path.equals("/error");
        log.debug("Path: {}, Method: {}, Is Public: {}", path, method, isPublic);
        return isPublic;
    }
}