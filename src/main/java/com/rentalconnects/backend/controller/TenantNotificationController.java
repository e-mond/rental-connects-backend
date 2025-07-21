package com.rentalconnects.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rentalconnects.backend.dto.NotificationDTO;
import com.rentalconnects.backend.service.NotificationService;

/**
 * Controller class for handling notification-related requests for tenants.
 */
@RestController
@RequestMapping("/api/tenant")
public class TenantNotificationController {

    private final NotificationService notificationService;

   
    public TenantNotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Retrieves all notifications for the authenticated tenant.
     *
     * @param userDetails The authenticated user's details.
     * @return ResponseEntity containing the list of NotificationDTOs.
     */
    @GetMapping("/notifications")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<List<NotificationDTO>> getNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {
        String tenantId = userDetails.getUsername(); // Email as tenantId
        List<NotificationDTO> notifications = notificationService.getNotificationsForTenant(tenantId);
        return ResponseEntity.ok(notifications);
    }
}