package com.rentalconnects.backend.controller;

import com.rentalconnects.backend.dto.AuthRequest;
import com.rentalconnects.backend.dto.RegisterRequest;
import com.rentalconnects.backend.dto.RegisterResponse;
import com.rentalconnects.backend.dto.SigninResponse;
import com.rentalconnects.backend.model.User;
import com.rentalconnects.backend.service.CustomIdService;
import com.rentalconnects.backend.service.EmailService;
import com.rentalconnects.backend.service.JwtService;
import com.rentalconnects.backend.service.SmsService;
import com.rentalconnects.backend.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CustomIdService customIdService;
    private final EmailService emailService;
    private final SmsService smsService;

    @Value("${app.base-url:http://localhost:3000}")
    private String BASE_URL;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder,
                          JwtService jwtService, CustomIdService customIdService,
                          EmailService emailService, SmsService smsService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.customIdService = customIdService;
        this.emailService = emailService;
        this.smsService = smsService;
    }

    @PostMapping("/signup")
    public ResponseEntity<RegisterResponse> signup(@RequestBody @Valid RegisterRequest request) {
        logger.info("Received signup request for email: {} at {}", request.getEmail(), Instant.now());
        try {
            if (userService.getUserByEmail(request.getEmail().trim().toLowerCase()).isPresent()) {
                logger.warn("Email already in use: {} at {}", request.getEmail(), Instant.now());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new RegisterResponse("Email is already in use", null, null, null));
            }

            User user = new User();
            user.setEmail(request.getEmail().trim().toLowerCase());
            logger.debug("Encoding password for signup: email={} at {}", request.getEmail(), Instant.now());
            String rawPassword = request.getPassword().trim();
            String encodedPassword = passwordEncoder.encode(rawPassword);
            logger.debug("Signup - Raw password length: {}, Encoded password length: {} at {}", rawPassword.length(), encodedPassword.length(), Instant.now());
            user.setPassword(encodedPassword);
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setFullName((request.getFirstName() != null ? request.getFirstName() : "") +
                    (request.getFirstName() != null && request.getLastName() != null ? " " : "") +
                    (request.getLastName() != null ? request.getLastName() : ""));
            user.setRole(request.getRole().toUpperCase());
            user.setPhoneNumber(request.getPhoneNumber());
            user.setLanguage(request.getLanguage());
            user.setTimeZone(request.getTimeZone());
            user.setCurrency(request.getCurrency());
            user.setEmailNotifications(request.isEmailNotifications());
            user.setAppNotifications(request.isPushNotifications());
            user.setSmsNotifications(request.isSmsNotifications());
            user.setBusinessName(request.getBusinessName());
            user.setPropertyManagementExperience(request.getPropertyManagementExperience());
            user.setEmployment(request.getEmployment());
            user.setRentalHistory(request.getRentalHistory());
            user.setAddress(null);
            user.setProfilePic(null);
            user.setPublicKey(null);

            String baseUsername = (request.getFirstName() + "." + (request.getLastName() != null ? request.getLastName() : ""))
                    .toLowerCase()
                    .replaceAll("[^a-z0-9.]", "");
            String username = baseUsername;
            int suffix = 1;
            while (userService.getUserByUsername(username).isPresent()) {
                username = baseUsername + suffix;
                suffix++;
            }
            user.setUsername(username);

            user.setCustomId(customIdService.generateCustomId(user.getRole(), user.getFirstName(), user.getLastName()));

            User savedUser = userService.createUser(user);
            logger.info("User saved successfully: {}, customId: {}, username: {}, fullName: {} at {}",
                    savedUser.getEmail(), savedUser.getCustomId(), savedUser.getUsername(), savedUser.getFullName(), Instant.now());

            String token = jwtService.generateToken(savedUser.getEmail());
            logger.debug("Generated token for user: {} at {}", savedUser.getEmail(), Instant.now());
            return ResponseEntity.ok(new RegisterResponse(
                    "User registered successfully",
                    savedUser.getId(),
                    savedUser.getRole(),
                    token
            ));
        } catch (Exception ex) {
            logger.error("Signup failed for: {} at {} - Error: {}", request.getEmail(), Instant.now(), ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new RegisterResponse("Signup failed: " + ex.getMessage(), null, null, null));
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<SigninResponse> signIn(@RequestBody @Valid AuthRequest loginRequest) {
        logger.info("Received sign-in request for email: {} at {}", loginRequest.getEmail(), Instant.now());
        try {
            User user = userService.getUserByEmail(loginRequest.getEmail().trim().toLowerCase())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid email: " + loginRequest.getEmail()));
            logger.debug("User found: email={}, stored hash length={} at {}", user.getEmail(), user.getPassword().length(), Instant.now());

            String inputPassword = loginRequest.getPassword().trim();
            logger.debug("Input password length: {} at {}", inputPassword.length(), Instant.now());
            boolean passwordMatches = passwordEncoder.matches(inputPassword, user.getPassword());
            logger.debug("Password match result: {} at {}", passwordMatches, Instant.now());

            if (!passwordMatches) {
                logger.warn("Password mismatch for email: {} at {}", loginRequest.getEmail(), Instant.now());
                throw new IllegalArgumentException("Invalid password");
            }

            String role = user.getRole().toUpperCase();
            if (!role.equals("LANDLORD") && !role.equals("TENANT")) {
                logger.warn("Invalid role for user: {} at {}", user.getEmail(), Instant.now());
                throw new IllegalArgumentException("Invalid user role: " + role);
            }

            String token = jwtService.generateToken(user.getEmail());
            logger.debug("Generated token for user: {} at {}", user.getEmail(), Instant.now());
            SigninResponse response = new SigninResponse(token, user.getId(), user.getCustomId(), user.getRole());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            logger.error("Sign-in failed: {} at {}", ex.getMessage(), Instant.now());
            throw ex;
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> request) {
        logger.info("Received forgot password request for email: {} at {}", request.get("email"), Instant.now());
        try {
            String email = request.get("email").trim().toLowerCase();
            String method = request.get("method") != null ? request.get("method").toLowerCase() : null;
            User user = userService.getUserByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Email not found: " + email));

            String resetToken = UUID.randomUUID().toString();
            long expirationTime = System.currentTimeMillis() + 3600000; // 1 hour
            user.setResetPasswordToken(resetToken);
            user.setResetPasswordExpires(expirationTime);
            userService.updateUser(user.getEmail(), user);

            String resetLink = BASE_URL + "/reset-password?token=" + resetToken;
            logger.info("Password reset link for {}: {} at {}", email, resetLink, Instant.now());

            if ("email".equals(method) || (method == null && user.isEmailNotifications() && user.getEmail() != null)) {
                if (user.getEmail() == null) {
                    throw new IllegalArgumentException("Email address not available for this user");
                }
                emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
                logger.info("Sent password reset email to {} at {}", user.getEmail(), Instant.now());
            } else if ("sms".equals(method) || (method == null && user.isSmsNotifications() && user.getPhoneNumber() != null)) {
                if (user.getPhoneNumber() == null) {
                    throw new IllegalArgumentException("Phone number not available for this user");
                }
                smsService.sendPasswordResetSms(user.getPhoneNumber(), resetLink);
                logger.info("Sent password reset SMS to {} at {}", user.getPhoneNumber(), Instant.now());
            } else {
                logger.warn("No valid notification method for user: {} at {}", email, Instant.now());
                throw new IllegalArgumentException("No valid notification method available. Please update your settings.");
            }

            Map<String, String> response = new HashMap<>();
            response.put("message", "Password reset link sent successfully");
            response.put("role", user.getRole());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            logger.error("Forgot password failed: {} at {}", ex.getMessage(), Instant.now());
            throw ex;
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> request) {
        logger.info("Received reset password request at {}", Instant.now());
        try {
            String token = request.get("token");
            String newPassword = request.get("newPassword").trim();
            User user = userService.getUserByResetToken(token)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset token"));

            if (user.getResetPasswordExpires() < System.currentTimeMillis()) {
                throw new IllegalArgumentException("Reset token has expired");
            }

            user.setPassword(passwordEncoder.encode(newPassword));
            user.setResetPasswordToken(null);
            user.setResetPasswordExpires(null);
            userService.updateUser(user.getEmail(), user);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Password reset successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            logger.error("Reset password failed: {} at {}", ex.getMessage(), Instant.now());
            throw ex;
        }
    }

    @GetMapping("/debug-password")
    public ResponseEntity<Map<String, String>> debugPassword(@RequestParam String email, @RequestParam String password) {
        logger.info("Debugging password for email: {} at {}", email, Instant.now());
        Map<String, String> response = new HashMap<>();
        try {
            User user = userService.getUserByEmail(email.trim().toLowerCase())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            boolean matches = passwordEncoder.matches(password.trim(), user.getPassword());
            response.put("storedHash", user.getPassword());
            response.put("passwordMatches", String.valueOf(matches));
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            response.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestBody Map<String, String> request) {
        logger.info("Received token refresh request at {}", Instant.now());
        try {
            String oldToken = request.get("token");
            String email = jwtService.extractEmail(oldToken);
            if (email != null && jwtService.validateToken(oldToken, email)) {
                String newToken = jwtService.generateToken(email);
                Map<String, String> response = new HashMap<>();
                response.put("token", newToken);
                logger.debug("Generated new token for email: {} at {}", email, Instant.now());
                return ResponseEntity.ok(response);
            }
            Map<String, String> error = new HashMap<>();
            error.put("message", "Invalid refresh token");
            logger.warn("Invalid refresh token for email: {} at {}", email, Instant.now());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception ex) {
            logger.error("Token refresh failed: {} at {}", ex.getMessage(), Instant.now());
            Map<String, String> error = new HashMap<>();
            error.put("message", "Token refresh failed: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/welcome")
    @PreAuthorize("hasAnyAuthority('ROLE_LANDLORD', 'ROLE_TENANT')")
    public ResponseEntity<String> welcome(@AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Received welcome request for user: {} at {}", userDetails.getUsername(), Instant.now());
        return ResponseEntity.ok("Welcome, " + userDetails.getUsername() + "!");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("Request failed: {} at {}", ex.getMessage(), Instant.now());
        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception ex) {
        logger.error("Unhandled exception: {} at {}", ex.getMessage(), Instant.now(), ex);
        Map<String, String> error = new HashMap<>();
        error.put("message", "An error occurred: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}