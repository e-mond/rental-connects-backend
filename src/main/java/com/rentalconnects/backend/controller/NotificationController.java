package com.rentalconnects.backend.controller;

import com.rentalconnects.backend.dto.NotificationDTO;
import com.rentalconnects.backend.model.Notification;
import com.rentalconnects.backend.service.NotificationService;
import com.rentalconnects.backend.util.AuthUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class NotificationController {

    private final NotificationService notificationService;
    private final AuthUtils authUtils;

    public NotificationController(NotificationService notificationService, AuthUtils authUtils) {
        this.notificationService = notificationService;
        this.authUtils = authUtils;
    }

    @GetMapping("/notifications")
    @PreAuthorize("hasAnyAuthority('ROLE_LANDLORD', 'ROLE_TENANT')")
    public ResponseEntity<List<NotificationDTO>> getNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {
        String userId = authUtils.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        String role = authUtils.getCurrentUserRole();
        List<NotificationDTO> notifications = switch (role) {
            case "LANDLORD" -> notificationService.getNotificationsForLandlord(userId);
            case "TENANT" -> notificationService.getNotificationsForTenant(userId);
            default -> throw new IllegalArgumentException("Invalid role: " + role);
        };
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/notifications/landlord/all")
    @PreAuthorize("hasAuthority('ROLE_LANDLORD')")
    public ResponseEntity<List<NotificationDTO>> getNotificationsForLandlord(
            @AuthenticationPrincipal UserDetails userDetails) {
        String landlordId = authUtils.getCurrentUserId();
        if (landlordId == null) {
            return ResponseEntity.badRequest().build();
        }
        List<NotificationDTO> notifications = notificationService.getNotificationsForLandlord(landlordId);
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/tenant/notifications")
    @PreAuthorize("hasAuthority('ROLE_TENANT')")
    public ResponseEntity<Void> sendNotificationForTenant(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody NotificationDTO notificationDTO) {
        String tenantId = authUtils.getCurrentUserId();
        if (tenantId == null) {
            return ResponseEntity.badRequest().build();
        }
        NotificationDTO finalNotification = new NotificationDTO();
        finalNotification.setTenantId(tenantId);
        finalNotification.setLandlordId(notificationDTO.getLandlordId());
        finalNotification.setMessage(notificationDTO.getMessage());
        finalNotification.setIsRead(false);
        finalNotification.setCreatedAt(LocalDateTime.now().toString());
        notificationService.sendNotification(convertToEntity(finalNotification));
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/landlord/notifications/{id}")
    @PreAuthorize("hasAuthority('ROLE_LANDLORD')")
    public ResponseEntity<NotificationDTO> updateNotificationStatus(
            @PathVariable String id,
            @RequestBody Map<String, Boolean> request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String landlordId = authUtils.getCurrentUserId();
        if (landlordId == null) {
            return ResponseEntity.badRequest().build();
        }
        Boolean isRead = request.get("isRead");
        if (isRead == null) {
            throw new IllegalArgumentException("isRead is required");
        }
        NotificationDTO updated = notificationService.updateNotificationStatus(id, landlordId, isRead);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/landlord/notifications")
    @PreAuthorize("hasAuthority('ROLE_LANDLARD')")
    public ResponseEntity<Void> clearNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {
        String landlordId = authUtils.getCurrentUserId();
        if (landlordId == null) {
            return ResponseEntity.badRequest().build();
        }
        notificationService.clearNotifications(landlordId);
        return ResponseEntity.ok().build();
    }

    private Notification convertToEntity(NotificationDTO dto) {
        Notification notification = new Notification();
        notification.setId(dto.getId());
        notification.setLandlordId(dto.getLandlordId());
        notification.setTenantId(dto.getTenantId());
        notification.setMessage(dto.getMessage());
        notification.setIsRead(dto.getIsRead());
        notification.setCreatedAt(LocalDateTime.parse(dto.getCreatedAt()));
        notification.setRecipientId(dto.getLandlordId());
        return notification;
    }
}