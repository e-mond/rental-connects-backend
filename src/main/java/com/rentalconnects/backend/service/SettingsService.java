package com.rentalconnects.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.rentalconnects.backend.model.User;
import com.rentalconnects.backend.repository.UserRepository;

/**
 * Service for managing user settings.
 */
@Service
public class SettingsService {

    private static final Logger log = LoggerFactory.getLogger(SettingsService.class);

    private final UserRepository userRepository;

    public SettingsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Fetches user settings by user ID.
     *
     * @param userId The ID of the user
     * @return SettingsDTO containing user settings
     * @throws RuntimeException if user is not found
     */
    public SettingsDTO getUserSettings(String userId) {
        log.info("Fetching settings for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);
                    return new RuntimeException("User not found with ID: " + userId);
                });

        return new SettingsDTO(
                user.getLanguage(),
                user.getTimeZone(),
                user.getCurrency(),
                user.isEmailNotifications(),
                user.isAppNotifications(),
                user.isSmsNotifications()
        );
    }

    /**
     * Updates user settings by user ID.
     *
     * @param userId      The ID of the user
     * @param settingsDTO The settings to update
     * @throws RuntimeException if user is not found
     */
    public void updateUserSettings(String userId, SettingsDTO settingsDTO) {
        log.info("Updating settings for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);
                    return new RuntimeException("User not found with ID: " + userId);
                });

        user.setLanguage(settingsDTO.getLanguage());
        user.setTimeZone(settingsDTO.getTimeZone());
        user.setCurrency(settingsDTO.getCurrency());
        user.setEmailNotifications(settingsDTO.getEmailNotifications());
        user.setAppNotifications(settingsDTO.getAppNotifications());
        user.setSmsNotifications(settingsDTO.getSmsNotifications());

        userRepository.save(user);
        log.info("Settings updated for user ID: {}", userId);
    }

    /**
     * DTO for settings data.
     */
    public static class SettingsDTO {
        private String language;
        private String timeZone;
        private String currency;
        private boolean emailNotifications;
        private boolean appNotifications;
        private boolean smsNotifications;

        // Constructor
        public SettingsDTO(String language, String timeZone, String currency,
                           Boolean emailNotifications, Boolean appNotifications, Boolean smsNotifications) {
            this.language = language;
            this.timeZone = timeZone;
            this.currency = currency;
            this.emailNotifications = emailNotifications != null ? emailNotifications : false;
            this.appNotifications = appNotifications != null ? appNotifications : false;
            this.smsNotifications = smsNotifications != null ? smsNotifications : false;
        }

        // Getters and Setters
        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getTimeZone() {
            return timeZone;
        }

        public void setTimeZone(String timeZone) {
            this.timeZone = timeZone;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public boolean getEmailNotifications() {
            return emailNotifications;
        }

        public void setEmailNotifications(boolean emailNotifications) {
            this.emailNotifications = emailNotifications;
        }

        public boolean getAppNotifications() {
            return appNotifications;
        }

        public void setAppNotifications(boolean appNotifications) {
            this.appNotifications = appNotifications;
        }

        public boolean getSmsNotifications() {
            return smsNotifications;
        }

        public void setSmsNotifications(boolean smsNotifications) {
            this.smsNotifications = smsNotifications;
        }
    }

    // Getter for userRepository
    public UserRepository getUserRepository() {
        return userRepository;
    }
}