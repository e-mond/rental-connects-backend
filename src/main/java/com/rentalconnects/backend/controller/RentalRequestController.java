package com.rentalconnects.backend.controller;

// imports for spring web, security, and project-specific services and utilities
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rentalconnects.backend.service.RentalRequestService;
import com.rentalconnects.backend.service.RentalRequestService.RentalRequestDTO;
import com.rentalconnects.backend.util.AuthUtils;

// handles rental request-related api endpoints for tenants and landlords in rentalconnects
@RestController
@RequestMapping("/api/rentalrequests")
public class RentalRequestController {

    // service for rental request operations and utility for authentication
    private final RentalRequestService rentalRequestService;
    private final AuthUtils authUtils;

    // injects dependencies via constructor
    public RentalRequestController(RentalRequestService rentalRequestService, AuthUtils authUtils) {
        this.rentalRequestService = rentalRequestService;
        this.authUtils = authUtils;
    }

    // fetches rental requests for the authenticated user (tenant or landlord)
    @GetMapping
    @PreAuthorize("hasAnyRole('TENANT', 'LANDLORD')")
    public ResponseEntity<List<RentalRequestDTO>> getRentalRequests() {
        // retrieves user id from authentication context
        String userId = authUtils.getCurrentUserId();
        // checks if user is authenticated
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        // fetches rental requests for the user
        List<RentalRequestDTO> requests = rentalRequestService.getTenantRequests(userId);
        // returns list of rental requests
        return ResponseEntity.ok(requests);
    }
}