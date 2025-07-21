package com.rentalconnects.backend.controller;

import com.rentalconnects.backend.model.Activity;
import com.rentalconnects.backend.model.Viewing;
import com.rentalconnects.backend.repository.ViewingRepository;
import com.rentalconnects.backend.service.ActivityService;
import com.rentalconnects.backend.dto.ActivityDTO;
import com.rentalconnects.backend.dto.ActivityRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ActivityController {
    private static final Logger logger = LoggerFactory.getLogger(ActivityController.class);
    private final ActivityService activityService;
    private final ViewingRepository viewingRepository;

    public ActivityController(ActivityService activityService, ViewingRepository viewingRepository) {
        this.activityService = activityService;
        this.viewingRepository = viewingRepository;
    }

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("ActivityController is mapped correctly!");
    }

    @GetMapping("/tenant/activity")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<List<ActivityDTO>> getRecentActivityForTenant() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Authentication: {}", auth != null ? auth.getName() + ", Roles: " + auth.getAuthorities() : "null");
        String tenantId = auth != null ? auth.getName() : null;
        logger.info("Tenant ID: {}", tenantId);
        if (tenantId == null) {
            logger.error("Tenant ID is null - Authentication failure");
            throw new IllegalStateException("Authentication is missing or invalid");
        }
        List<Activity> activities = activityService.getRecentActivitiesForTenant(tenantId);
        List<ActivityDTO> dtos = activities.stream().map(activity -> {
            ActivityDTO dto = new ActivityDTO();
            dto.setId(activity.getId());
            dto.setType(activity.getType());
            dto.setMessage(activity.getMessage());
            dto.setTime(activity.getTime());
            dto.setEntityId(activity.getEntityId());
            dto.setPropertyId(activity.getPropertyId());
            if ("SCHEDULED_VIEWING".equals(activity.getType()) || "VIEWING_RESCHEDULED".equals(activity.getType())) {
                viewingRepository.findById(activity.getEntityId())
                        .ifPresent(viewing -> {
                            dto.setPropertyName(viewing.getPropertyName());
                            dto.setPropertyId(viewing.getPropertyId());
                        });
            }
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/landlord/activity")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<List<ActivityDTO>> getRecentActivityForLandlord() {
        String landlordId = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("Landlord ID: {}", landlordId);
        List<Activity> activities = activityService.getRecentActivitiesForLandlord(landlordId);
        List<ActivityDTO> dtos = activities.stream().map(activity -> {
            ActivityDTO dto = new ActivityDTO();
            dto.setId(activity.getId());
            dto.setType(activity.getType());
            dto.setMessage(activity.getMessage());
            dto.setTime(activity.getTime());
            dto.setEntityId(activity.getEntityId());
            dto.setPropertyId(activity.getPropertyId());
            if ("SCHEDULED_VIEWING".equals(activity.getType()) || "VIEWING_RESCHEDULED".equals(activity.getType())) {
                viewingRepository.findById(activity.getEntityId())
                        .ifPresent(viewing -> {
                            dto.setPropertyName(viewing.getPropertyName());
                            dto.setPropertyId(viewing.getPropertyId());
                        });
            }
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/activity")
    @PreAuthorize("hasAnyRole('TENANT', 'LANDLORD')")
    public ResponseEntity<Void> logActivity(@Valid @RequestBody ActivityRequest request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("Logging activity for userId: {}, type: {}", userId, request.getType());
        String tenantId = request.getType().startsWith("VIEWING_") ? userId : null;
        String landlordId = request.getType().startsWith("VIEWING_") ? null : userId;
        String propertyId = request.getPropertyId();
        if (request.getType().equals("SCHEDULED_VIEWING") || request.getType().equals("VIEWING_RESCHEDULED")) {
            activityService.createViewingActivity(
                tenantId,
                request.getEntityId(),
                userId,
                request.getType()
            );
        } else {
            if (request.getType().startsWith("VIEWING_")) {
                propertyId = viewingRepository.findById(request.getEntityId())
                        .map(Viewing::getPropertyId)
                        .orElse(propertyId);
            }
            activityService.logActivity(
                tenantId,
                landlordId,
                request.getType(),
                request.getMessage(),
                request.getEntityId(),
                propertyId
            );
        }
        return ResponseEntity.ok().build();
    }
}