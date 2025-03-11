package com.rentalconnects.backend.service;

import com.rentalconnects.backend.model.User;
import com.rentalconnects.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Create a new user with an encoded password
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // Find a user by their email address
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Find a user by their unique ID
    public User findById(String id) {
        return userRepository.findById(id).orElse(null);
    }
}
