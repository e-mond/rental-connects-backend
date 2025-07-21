package com.rentalconnects.backend.controller;

import com.rentalconnects.backend.dto.UserDTO;
import com.rentalconnects.backend.model.User;
import com.rentalconnects.backend.security.JwtAuthenticationFilter.CustomPrincipal;
import com.rentalconnects.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<UserDTO> createUser(@RequestBody User user) {
        logger.info("Creating user: {} at {}", user.getEmail(), java.time.Instant.now());
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(new UserDTO(createdUser));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        logger.info("Fetching user by email: {} at {}", email, java.time.Instant.now());
        UserDTO user = userService.getUserDTOByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String userId) {
        logger.info("Fetching user by ID: {} at {}", userId, java.time.Instant.now());
        UserDTO user = userService.getUserDTOById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));
        return ResponseEntity.ok(user);
    }

    @PutMapping("/update/{email}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable String email, @RequestBody User updatedUser) {
        logger.info("Updating user: {} at {}", email, java.time.Instant.now());
        UserDTO updated = userService.updateUser(email, updatedUser);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/profile-picture/{userId}")
    public ResponseEntity<Void> updateProfilePicture(@PathVariable String userId, @RequestParam String profilePic) {
        logger.info("Updating profile picture for user ID: {} at {}", userId, java.time.Instant.now());
        userService.updateProfilePicture(userId, profilePic);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/profile/{userId}")
    public ResponseEntity<UserDTO> updateProfile(@PathVariable String userId, @RequestBody User profileData) {
        logger.info("Updating profile for user ID: {} at {}", userId, java.time.Instant.now());
        UserDTO updated = userService.updateProfile(userId, profileData);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/password/{userId}")
    public ResponseEntity<Void> updatePassword(@PathVariable String userId,
                                              @RequestParam String currentPassword,
                                              @RequestParam String newPassword) {
        logger.info("Updating password for user ID: {} at {}", userId, java.time.Instant.now());
        userService.updatePassword(userId, currentPassword, newPassword);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/notifications/{userId}")
    public ResponseEntity<Void> updateNotifications(@PathVariable String userId,
                                                   @RequestBody Map<String, Boolean> notificationsData) {
        logger.info("Updating notifications for user ID: {} at {}", userId, java.time.Instant.now());
        userService.updateNotifications(userId, notificationsData);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> testEndpoint() {
        logger.info("Test endpoint called at {}", java.time.Instant.now());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Test successful at " + new java.util.Date());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/email-secure/{email}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getUserByEmailSecure(@PathVariable String email) {
        logger.info("Fetching user securely by email: {} at {}", email, java.time.Instant.now());
        UserDTO user = userService.getUserDTOByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return ResponseEntity.ok(user);
    }

    @GetMapping("/id-secure/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getUserByIdSecure(@PathVariable String userId) {
        logger.info("Fetching user securely by ID: {} at {}", userId, java.time.Instant.now());
        UserDTO user = userService.getUserDTOById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));
        return ResponseEntity.ok(user);
    }

    @GetMapping("/id-admin/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> getUserByIdAdmin(@PathVariable String userId) {
        logger.info("Fetching user by ID for admin: {} at {}", userId, java.time.Instant.now());
        UserDTO user = userService.getUserDTOById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));
        return ResponseEntity.ok(user);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCurrentUser() {
        logger.info("Received request for /api/users/me at {}", java.time.Instant.now());
        try {
            CustomPrincipal principal = (CustomPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            logger.info("Principal: username={}, userId={} at {}", principal.getUsername(), principal.getUserId(), java.time.Instant.now());
            String userId = principal.getUserId();
            logger.debug("Fetching user for userId: {} at {}", userId, java.time.Instant.now());
            UserDTO user = userService.getUserDTOById(userId)
                    .orElseThrow(() -> {
                        logger.error("User not found with ID: {} at {}", userId, java.time.Instant.now());
                        return new UsernameNotFoundException("User not found with ID: " + userId);
                    });
            logger.debug("Successfully retrieved user: {} at {}", user.getEmail(), java.time.Instant.now());
            return ResponseEntity.ok(user);
        } catch (UsernameNotFoundException e) {
            logger.error("Failed to retrieve current user: {} at {}", e.getMessage(), java.time.Instant.now());
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            logger.error("Unexpected error: {} at {}", e.getMessage(), java.time.Instant.now(), e);
            Map<String, String> error = new HashMap<>();
            error.put("message", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}