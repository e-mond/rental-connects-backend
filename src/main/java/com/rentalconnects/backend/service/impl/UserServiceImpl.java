package com.rentalconnects.backend.service.impl;

import com.rentalconnects.backend.dto.UserDTO;
import com.rentalconnects.backend.model.User;
import com.rentalconnects.backend.repository.UserRepository;
import com.rentalconnects.backend.service.CustomIdService;
import com.rentalconnects.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomIdService customIdService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, CustomIdService customIdService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.customIdService = customIdService;
    }

    @Override
    public User createUser(User user) {
        logger.info("Creating user with email: {}", user.getEmail());
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        try {
            if (user.getCustomId() == null) {
                user.setCustomId(customIdService.generateCustomId(user.getRole(), user.getFirstName(), user.getLastName()));
            }
            if (!user.getPassword().startsWith("$2a$")) {
                user.setPassword(passwordEncoder.encode(user.getPassword().trim()));
                logger.debug("Encoded password for email: {}", user.getEmail());
            } else {
                logger.debug("Password already hashed, skipping re-encoding for email: {}", user.getEmail());
            }
            user.setFullName((user.getFirstName() != null ? user.getFirstName() : "") +
                            (user.getFirstName() != null && user.getLastName() != null ? " " : "") +
                            (user.getLastName() != null ? user.getLastName() : ""));
            User savedUser = userRepository.save(user);
            logger.info("User saved successfully: {}, customId: {}, username: {}, fullName: {}",
                        savedUser.getEmail(), savedUser.getCustomId(), savedUser.getUsername(), savedUser.getFullName());
            return savedUser;
        } catch (Exception e) {
            logger.error("Failed to save user: {}", user.getEmail(), e);
            throw new RuntimeException("Failed to create user: " + e.getMessage());
        }
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email must not be null or empty");
        }
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username must not be null or empty");
        }
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> getUserById(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID must not be null or empty");
        }
        return userRepository.findById(userId);
    }

    @Override
    public Optional<UserDTO> getUserDTOById(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID must not be null or empty");
        }
        return userRepository.findById(userId).map(UserDTO::new);
    }

    @Override
    public Optional<UserDTO> getUserDTOByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email must not be null or empty");
        }
        return userRepository.findByEmail(email).map(UserDTO::new);
    }

    @Override
    public UserDTO updateUser(String email, User updatedUser) {
        logger.info("Updating user with email: {}", email);
        User existingUser = getUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        existingUser.setFirstName(updatedUser.getFirstName() != null ? updatedUser.getFirstName() : existingUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName() != null ? updatedUser.getLastName() : existingUser.getLastName());
        existingUser.setFullName((existingUser.getFirstName() != null ? existingUser.getFirstName() : "") +
                                (existingUser.getFirstName() != null && existingUser.getLastName() != null ? " " : "") +
                                (existingUser.getLastName() != null ? existingUser.getLastName() : ""));
        existingUser.setPhoneNumber(updatedUser.getPhoneNumber() != null ? updatedUser.getPhoneNumber() : existingUser.getPhoneNumber());
        existingUser.setAddress(updatedUser.getAddress() != null ? updatedUser.getAddress() : existingUser.getAddress());
        existingUser.setProfilePic(updatedUser.getProfilePic() != null ? updatedUser.getProfilePic() : existingUser.getProfilePic());
        existingUser.setLanguage(updatedUser.getLanguage() != null ? updatedUser.getLanguage() : existingUser.getLanguage());
        existingUser.setTimeZone(updatedUser.getTimeZone() != null ? updatedUser.getTimeZone() : existingUser.getTimeZone());
        existingUser.setCurrency(updatedUser.getCurrency() != null ? updatedUser.getCurrency() : existingUser.getCurrency());
        existingUser.setEmailNotifications(updatedUser.isEmailNotifications());
        existingUser.setAppNotifications(updatedUser.isAppNotifications());
        existingUser.setSmsNotifications(updatedUser.isSmsNotifications());
        existingUser.setBusinessName(updatedUser.getBusinessName() != null ? updatedUser.getBusinessName() : existingUser.getBusinessName());
        existingUser.setPropertyManagementExperience(updatedUser.getPropertyManagementExperience() != null ? updatedUser.getPropertyManagementExperience() : existingUser.getPropertyManagementExperience());
        existingUser.setEmployment(updatedUser.getEmployment() != null ? updatedUser.getEmployment() : existingUser.getEmployment());
        existingUser.setRentalHistory(updatedUser.getRentalHistory() != null ? updatedUser.getRentalHistory() : existingUser.getRentalHistory());
        existingUser.setPublicKey(updatedUser.getPublicKey() != null ? updatedUser.getPublicKey() : existingUser.getPublicKey());
        User savedUser = userRepository.save(existingUser);
        return new UserDTO(savedUser);
    }

    @Override
    public void updateProfilePicture(String userId, String profilePic) {
        logger.info("Updating profile picture for userId: {}", userId);
        User user = getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        user.setProfilePic(profilePic);
        userRepository.save(user);
    }

    @Override
    public UserDTO updateProfile(String userId, User profileData) {
        logger.info("Updating profile for userId: {}", userId);
        User user = getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        user.setFirstName(profileData.getFirstName() != null ? profileData.getFirstName() : user.getFirstName());
        user.setLastName(profileData.getLastName() != null ? profileData.getLastName() : user.getLastName());
        user.setFullName((user.getFirstName() != null ? user.getFirstName() : "") +
                        (user.getFirstName() != null && user.getLastName() != null ? " " : "") +
                        (user.getLastName() != null ? user.getLastName() : ""));
        user.setPhoneNumber(profileData.getPhoneNumber() != null ? profileData.getPhoneNumber() : user.getPhoneNumber());
        user.setAddress(profileData.getAddress() != null ? profileData.getAddress() : user.getAddress());
        user.setBusinessName(profileData.getBusinessName() != null ? profileData.getBusinessName() : user.getBusinessName());
        user.setPropertyManagementExperience(profileData.getPropertyManagementExperience() != null ? profileData.getPropertyManagementExperience() : user.getPropertyManagementExperience());
        user.setEmployment(profileData.getEmployment() != null ? profileData.getEmployment() : user.getEmployment());
        user.setRentalHistory(profileData.getRentalHistory() != null ? profileData.getRentalHistory() : user.getRentalHistory());
        User savedUser = userRepository.save(user);
        return new UserDTO(savedUser);
    }

    @Override
    public void updatePassword(String userId, String currentPassword, String newPassword) {
        logger.info("Updating password for userId: {}", userId);
        User user = getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException("New password must be at least 8 characters long");
        }
        user.setPassword(passwordEncoder.encode(newPassword.trim()));
        userRepository.save(user);
    }

    @Override
    public void updateNotifications(String userId, Map<String, Boolean> notificationsData) {
        logger.info("Updating notifications for userId: {}", userId);
        User user = getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        if (notificationsData.containsKey("emailNotifications")) {
            user.setEmailNotifications(notificationsData.get("emailNotifications"));
        }
        if (notificationsData.containsKey("appNotifications")) {
            user.setAppNotifications(notificationsData.get("appNotifications"));
        }
        if (notificationsData.containsKey("smsNotifications")) {
            user.setSmsNotifications(notificationsData.get("smsNotifications"));
        }
        userRepository.save(user);
    }

    @Override
    public Optional<User> getUserByResetToken(String resetToken) {
        if (resetToken == null || resetToken.isBlank()) {
            throw new IllegalArgumentException("Reset token must not be null or empty");
        }
        return userRepository.findByResetPasswordToken(resetToken);
    }
}