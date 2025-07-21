package com.rentalconnects.backend.service;

import com.rentalconnects.backend.dto.UserDTO;
import com.rentalconnects.backend.model.User;

import java.util.Map;
import java.util.Optional;

public interface UserService {
    User createUser(User user);
    Optional<User> getUserByEmail(String email);
    Optional<User> getUserByUsername(String username);
    Optional<User> getUserById(String id);
    Optional<User> getUserByResetToken(String resetToken);
    UserDTO updateUser(String email, User updatedUser);
    void updateProfilePicture(String userId, String profilePic);
    UserDTO updateProfile(String userId, User profileData);
    void updatePassword(String userId, String currentPassword, String newPassword);
    void updateNotifications(String userId, Map<String, Boolean> notificationsData);
    Optional<UserDTO> getUserDTOById(String id);
    Optional<UserDTO> getUserDTOByEmail(String email);
}