package com.rentalconnects.backend.controller;

// imports for spring web, security, and project-specific services and dtos
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rentalconnects.backend.service.SettingsService;
import com.rentalconnects.backend.service.SettingsService.SettingsDTO;
import com.rentalconnects.backend.util.AuthUtils;

// handles user settings-related api endpoints for tenants and landlords in rentalconnects
@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    // service for settings operations and utility for authentication
    private final SettingsService settingsService;
    private final AuthUtils authUtils;

    // injects dependencies via constructor
    public SettingsController(SettingsService settingsService, AuthUtils authUtils) {
        this.settingsService = settingsService;
        this.authUtils = authUtils;
    }

    // fetches settings for the authenticated user (tenant or landlord)
    @GetMapping
    @PreAuthorize("hasAnyRole('TENANT', 'LANDLORD')")
    public ResponseEntity<SettingsDTO> getSettings() {
        // retrieves user id from authentication context
        String userId = authUtils.getCurrentUserId();
        // checks if user is authenticated
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        // fetches user settings
        SettingsDTO settings = settingsService.getUserSettings(userId);
        // returns user settings
        return ResponseEntity.ok(settings);
    }

    // updates settings for the authenticated user (tenant or landlord)
    @PutMapping
    @PreAuthorize("hasAnyRole('TENANT', 'LANDLORD')")
    public ResponseEntity<Void> updateSettings(@RequestBody SettingsDTO settingsDTO) {
        // retrieves user id from authentication context
        String userId = authUtils.getCurrentUserId();
        // checks if user is authenticated
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        // updates user settings
        settingsService.updateUserSettings(userId, settingsDTO);
        // returns success response
        return ResponseEntity.ok().build();
    }
}