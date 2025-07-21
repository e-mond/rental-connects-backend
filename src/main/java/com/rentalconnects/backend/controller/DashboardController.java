package com.rentalconnects.backend.controller;

// imports for logging, spring web, security, and project-specific utilities
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rentalconnects.backend.dto.LandlordDashboardDataDTO;
import com.rentalconnects.backend.service.DashboardService;
import com.rentalconnects.backend.util.AuthUtils;

// handles landlord dashboard api requests for rentalconnects
@RestController
@RequestMapping("/api/landlord/dashboard")
public class DashboardController {

    // logger for tracking dashboard-related events
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    // services for dashboard data and user authentication utilities
    private final DashboardService dashboardService;
    private final AuthUtils authUtils;

    // injects dependencies via constructor
    public DashboardController(DashboardService dashboardService, AuthUtils authUtils) {
        this.dashboardService = dashboardService;
        this.authUtils = authUtils;
    }

    // fetches dashboard data for authenticated landlord, restricted to landlord role
    @GetMapping
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<?> getDashboardData() {
        // retrieves current user id from authentication context
        String userId = authUtils.getCurrentUserId();
        // checks if user is authenticated
        if (userId == null || userId.trim().isEmpty()) {
            logger.warn("unauthorized access attempt: user id not found");
            return ResponseEntity.status(401).body(new ErrorResponse("user not authenticated"));
        }

        // fetches and returns dashboard data
        try {
            LandlordDashboardDataDTO dashboardData = dashboardService.getLandlordDashboardData(userId);
            logger.info("fetched dashboard data for landlord: {}", userId);
            return ResponseEntity.ok(dashboardData);
        } catch (Exception e) {
            logger.error("error fetching dashboard data for landlord: {}", userId, e);
            return ResponseEntity.status(500).body(new ErrorResponse("failed to fetch dashboard data"));
        }
    }
}

// dto for structured error responses
class ErrorResponse {
    private String message;

    // constructor for error message
    public ErrorResponse(String message) {
        this.message = message;
    }

    // getter for error message
    public String getMessage() {
        return message;
    }

    // setter for error message
    public void setMessage(String message) {
        this.message = message;
    }
}