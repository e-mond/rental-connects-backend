package com.rentalconnects.backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.rentalconnects.backend.config.JwtConfig;
import com.rentalconnects.backend.model.User;
import com.rentalconnects.backend.repository.UserRepository;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    private final JwtConfig jwtConfig;
    private final UserRepository userRepository;
    private final SecretKey signingKey;
    private final JwtParser jwtParser;

    public JwtService(JwtConfig jwtConfig, UserRepository userRepository) {
        this.jwtConfig = jwtConfig;
        this.userRepository = userRepository;
        this.signingKey = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
        this.jwtParser = Jwts.parser()
                             .verifyWith(signingKey)
                             .build();
        logger.info("JwtService initialized with secret length: {}, expiration: {} ms at {}", 
            jwtConfig.getSecret().length(), jwtConfig.getExpiration(), java.time.Instant.now());
    }

    public String generateToken(String email) {
        logger.debug("Generating token for email: {} at {}", email, java.time.Instant.now());
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> {
                    logger.error("User not found for email: {} at {}", email, java.time.Instant.now());
                    return new RuntimeException("User not found with email: " + email);
                });
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole());
        return createToken(claims, email);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        logger.debug("Creating token for subject: {} with claims: {} at {}", subject, claims, java.time.Instant.now());
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtConfig.getExpiration()))
                .signWith(signingKey)
                .compact();
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractId(String token) {
        return extractClaim(token, claims -> claims.get("id", String.class));
    }

    public String extractTenantId(String token) {
        return extractId(token);
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return jwtParser.parseSignedClaims(token).getPayload();
        } catch (Exception e) {
            logger.error("Error parsing JWT token: {} at {}", e.getMessage(), java.time.Instant.now());
            throw e;
        }
    }

    public boolean validateToken(String token, String email) {
        try {
            final String extractedEmail = extractEmail(token);
            boolean isValid = extractedEmail.equals(email.trim().toLowerCase()) && !isTokenExpired(token);
            logger.debug("Token validation for email: {} - Valid: {} at {}", email, isValid, 
                java.time.Instant.now());
            return isValid;
        } catch (Exception e) {
            logger.error("Token validation failed for token: {} - Error: {} at {}", 
                token.substring(0, Math.min(token.length(), 20)) + "...", e.getMessage(), java.time.Instant.now());
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        boolean isExpired = extractExpiration(token).before(new Date());
        logger.debug("Token expiration check: Expired: {} at {}", isExpired, 
            java.time.Instant.now());
        return isExpired;
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public JwtConfig getJwtConfig() { return jwtConfig; }
    public SecretKey getSigningKey() { return signingKey; }
    public UserRepository getUserRepository() { return userRepository; }
}