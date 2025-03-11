package com.rentalconnects.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rentalconnects.backend.model.User;
import com.rentalconnects.backend.security.JwtUtil;
import com.rentalconnects.backend.service.UserService;

@RestController // Marks this class as a REST controller
@RequestMapping("/api/auth") // Base URL for authentication endpoints
public class AuthController {

    @Autowired
    private UserService userService; // Service layer for user operations

    @Autowired
    private AuthenticationManager authenticationManager; // Handles authentication

    @Autowired
    private JwtUtil jwtUtil; // Utility for JWT token handling

    @Autowired
    private PasswordEncoder passwordEncoder; // Encrypts user passwords

    @PostMapping("/signup")
    public User signUp(@RequestBody User user) {
        // Hash the password before saving the user
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userService.createUser(user); // Save the new user in the database
    }

    @PostMapping("/signin")
    public String signIn(@RequestBody User user) {
        // Authenticate user credentials
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
        );
        if (authentication.isAuthenticated()) {
            return jwtUtil.generateToken(user.getEmail()); // Return a JWT token if authentication is successful
        }
        return "Invalid credentials"; // Return an error message if authentication fails
    }

    @GetMapping("/me")
    public User getCurrentUser() {
        // Get the authenticated user's email from the security context
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByEmail(email); // Retrieve the user details from the database
    }
}
