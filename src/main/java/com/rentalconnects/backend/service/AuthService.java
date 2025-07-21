package com.rentalconnects.backend.service;

import com.rentalconnects.backend.dto.RegisterResponse;
import com.rentalconnects.backend.model.User;
import com.rentalconnects.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public RegisterResponse register(User user) {
        log.info("Registering user with email: {}", user.getEmail());
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = userRepository.save(user);
            String token = jwtService.generateToken(savedUser.getEmail());
            log.info("User registered successfully: {}", savedUser.getEmail());
            return new RegisterResponse("Registration successful", savedUser.getId(), savedUser.getRole(), token);
        } catch (Exception e) {
            log.error("Registration failed for email: {}", user.getEmail(), e);
            throw new RuntimeException("Registration failed: " + e.getMessage());
        }
    }

    public String login(String email, String password) {
        log.info("Logging in user with email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (passwordEncoder.matches(password, user.getPassword())) {
            String token = jwtService.generateToken(email);
            log.info("Login successful for email: {}", email);
            return token;
        } else {
            log.warn("Invalid credentials for email: {}", email);
            throw new RuntimeException("Invalid credentials");
        }
    }
}