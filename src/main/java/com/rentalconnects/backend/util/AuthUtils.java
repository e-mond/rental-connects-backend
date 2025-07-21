package com.rentalconnects.backend.util;

import com.rentalconnects.backend.model.User;
import com.rentalconnects.backend.repository.UserRepository;
import com.rentalconnects.backend.security.JwtAuthenticationFilter.CustomPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtils {

    private static final Logger logger = LoggerFactory.getLogger(AuthUtils.class);
    private final UserRepository userRepository;

    public AuthUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
        logger.info("AuthUtils initialized at {}", java.time.Instant.now());
    }

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            logger.warn("No valid authentication found in SecurityContext at {}", java.time.Instant.now());
            throw new IllegalStateException("No authenticated user found");
        }

        Object principal = authentication.getPrincipal();
        final String email;
        final String userId;

        if (principal instanceof CustomPrincipal customPrincipal) {
            email = customPrincipal.getUsername();
            userId = customPrincipal.getUserId();
            logger.debug("Authenticated user: email={}, userId={} at {}", email, userId, java.time.Instant.now());
        } else if (principal instanceof String stringPrincipal) {
            email = stringPrincipal;
            userId = null;
            logger.debug("Authenticated user: email={} (String principal) at {}", email, java.time.Instant.now());
        } else {
            logger.warn("Unexpected principal type: {} at {}", principal != null ? principal.getClass().getName() : "null", java.time.Instant.now());
            throw new IllegalStateException("Invalid principal type: " + (principal != null ? principal.getClass().getName() : "null"));
        }

        if (userId != null) {
            return userRepository.findById(userId)
                    .orElseThrow(() -> {
                        logger.warn("User not found for userId: {} at {}", userId, java.time.Instant.now());
                        return new IllegalStateException("User not found with ID: " + userId);
                    });
        } else if (email != null) {
            return userRepository.findByEmail(email.trim().toLowerCase())
                    .orElseThrow(() -> {
                        logger.warn("User not found for email: {} at {}", email, java.time.Instant.now());
                        return new IllegalStateException("User not found with email: " + email);
                    });
        }

        logger.warn("No userId or email found in principal at {}", java.time.Instant.now());
        throw new IllegalStateException("No authenticated user found");
    }

    public String getCurrentUserId() {
        User user = getAuthenticatedUser();
        String userId = user.getId();
        logger.debug("Retrieved userId: {} at {}", userId, java.time.Instant.now());
        return userId;
    }

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            logger.debug("No authenticated username found at {}", java.time.Instant.now());
            return null;
        }
        Object principal = authentication.getPrincipal();
        String username = principal instanceof CustomPrincipal customPrincipal ? customPrincipal.getUsername() : principal.toString();
        logger.debug("Retrieved username: {} at {}", username, java.time.Instant.now());
        return username;
    }

    public String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            logger.debug("No authenticated role found at {}", java.time.Instant.now());
            return null;
        }
        String role = authentication.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        logger.debug("Retrieved user role: {} at {}", role, java.time.Instant.now());
        return role;
    }
}