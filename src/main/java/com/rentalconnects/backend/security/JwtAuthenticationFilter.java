package com.rentalconnects.backend.security;

import java.io.IOException;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.rentalconnects.backend.service.CustomUserDetailsService;
import com.rentalconnects.backend.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        logger.info("JwtAuthenticationFilter initialized with JwtService: {} and UserDetailsService: {}", 
            jwtService != null, userDetailsService != null);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        String method = request.getMethod();
        logger.debug("Processing request to: {} with method: {} at {}", path, method, java.time.Instant.now());

        // Skip JWT validation for public endpoints
        if ((path.equals("/api/auth/signin") && method.equals("POST")) ||
            (path.equals("/api/auth/signup") && method.equals("POST")) ||
            (path.equals("/api/auth/login") && method.equals("POST")) ||
            (path.equals("/api/properties") && method.equals("GET")) ||
            (path.startsWith("/api/properties/") && method.equals("GET")) ||
            (path.startsWith("/images/")) ||
            (path.startsWith("/uploads/properties/") && method.equals("GET")) ||
            (method.equals("OPTIONS"))) {
            logger.debug("Skipping JWT validation for public endpoint: {} at {}", path, java.time.Instant.now());
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        logger.debug("Authorization Header: {} at {}", header != null ? header : "null", java.time.Instant.now());
        if (header == null || !header.startsWith("Bearer ")) {
            logger.warn("No Authorization header or not a Bearer token for protected endpoint: {} at {}", path, java.time.Instant.now());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"No token provided or invalid format\"}");
            response.getWriter().flush();
            return;
        }

        String token = header.substring(7);
        logger.debug("Extracted Token: {} at {}", token.substring(0, Math.min(token.length(), 20)) + "...", java.time.Instant.now());
        try {
            String username = jwtService.extractEmail(token);
            String userId = jwtService.extractId(token);
            String role = jwtService.extractRole(token);
            logger.debug("Extracted Username: {}, User ID: {}, Role: {} at {}", username, userId, role, java.time.Instant.now());

            // Validate role
            if (!role.equals("LANDLORD") && !role.equals("TENANT")) {
                logger.warn("Invalid role: {} at {}", role, java.time.Instant.now());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Invalid user role: " + role + "\"}");
                response.getWriter().flush();
                return;
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwtService.validateToken(token, username)) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    CustomPrincipal principal = new CustomPrincipal(username, userId, Collections.singletonList(
                        new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role)));
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            principal, null, principal.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.debug("Authentication set for: {} with authorities: {} at {}", username, principal.getAuthorities(), java.time.Instant.now());
                } else {
                    logger.warn("Token validation failed for username: {} at {}", username, java.time.Instant.now());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Invalid or expired token\"}");
                    response.getWriter().flush();
                    return;
                }
            } else {
                logger.debug("Username null or authentication already set: {} at {}", 
                    SecurityContextHolder.getContext().getAuthentication() != null, java.time.Instant.now());
            }
        } catch (Exception e) {
            logger.error("Token processing error: {} at {}", e.getMessage(), java.time.Instant.now(), e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Token processing error: " + e.getMessage() + "\"}");
            response.getWriter().flush();
            return;
        }
        logger.debug("SecurityContext after processing: {} at {}", SecurityContextHolder.getContext().getAuthentication(), java.time.Instant.now());
        filterChain.doFilter(request, response);
    }

    public static class CustomPrincipal {
        private final String username;
        private final String userId;
        private final java.util.Collection<? extends org.springframework.security.core.GrantedAuthority> authorities;

        public CustomPrincipal(String username, String userId, java.util.Collection<? extends org.springframework.security.core.GrantedAuthority> authorities) {
            this.username = username;
            this.userId = userId;
            this.authorities = authorities;
        }

        public String getUsername() {
            return username;
        }

        public String getUserId() {
            return userId;
        }

        public java.util.Collection<? extends org.springframework.security.core.GrantedAuthority> getAuthorities() {
            return authorities;
        }

        @Override
        public String toString() {
            return "CustomPrincipal{username='" + username + "', userId='" + userId + "'}";
        }
    }
}