package com.rentalconnects.backend.controller;

import com.rentalconnects.backend.model.Viewing;
import com.rentalconnects.backend.service.ViewingService;
import com.rentalconnects.backend.service.ActivityService;
import com.rentalconnects.backend.util.AuthUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/viewings")
public class ViewingController {

    private final ViewingService viewingService;
    private final ActivityService activityService;
    private final AuthUtils authUtils;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public ViewingController(ViewingService viewingService, ActivityService activityService, AuthUtils authUtils) {
        this.viewingService = viewingService;
        this.activityService = activityService;
        this.authUtils = authUtils;
    }

    @PostMapping("/schedule")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<Map<String, Object>> scheduleViewing(
            @RequestParam String propertyId,
            @RequestParam String viewingDate,
            @RequestParam(required = false) String notes,
            @RequestParam(defaultValue = "false") boolean important) {
        String tenantId = authUtils.getCurrentUserId();
        if (tenantId == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Unable to authenticate user"));
        }
        LocalDateTime dateTime = LocalDateTime.parse(viewingDate, DATE_TIME_FORMATTER);

        Viewing viewing = viewingService.scheduleViewing(propertyId, tenantId, dateTime, notes, important);
        activityService.createViewingActivity(tenantId, viewing.getId(), tenantId, "SCHEDULED_VIEWING");

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Viewing scheduled successfully!");
        response.put("viewing", Map.of(
                "id", viewing.getId(),
                "propertyId", viewing.getPropertyId(),
                "propertyName", viewing.getPropertyName(),
                "tenantId", viewing.getTenantId(),
                "viewingDate", viewing.getViewingDate().toString(),
                "status", viewing.getStatus(),
                "notes", viewing.getNotes(),
                "important", viewing.isImportant()
        ));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reschedule")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<Map<String, Object>> rescheduleViewing(
            @RequestParam String viewingId,
            @RequestParam String viewingDate,
            @RequestParam(required = false) String notes,
            @RequestParam(defaultValue = "false") boolean important) {
        String tenantId = authUtils.getCurrentUserId();
        if (tenantId == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Unable to authenticate user"));
        }
        LocalDateTime dateTime = LocalDateTime.parse(viewingDate, DATE_TIME_FORMATTER);

        Viewing viewing = viewingService.rescheduleViewing(viewingId, tenantId, dateTime, notes, important);
        activityService.createViewingActivity(tenantId, viewing.getId(), tenantId, "VIEWING_RESCHEDULED");

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Viewing rescheduled successfully!");
        response.put("viewing", Map.of(
                "id", viewing.getId(),
                "propertyId", viewing.getPropertyId(),
                "propertyName", viewing.getPropertyName(),
                "tenantId", viewing.getTenantId(),
                "viewingDate", viewing.getViewingDate().toString(),
                "status", viewing.getStatus(),
                "notes", viewing.getNotes(),
                "important", viewing.isImportant()
        ));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cancel")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<Map<String, Object>> cancelViewing(
            @RequestParam String viewingId) {
        String tenantId = authUtils.getCurrentUserId();
        if (tenantId == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Unable to authenticate user"));
        }

        Viewing viewing = viewingService.cancelViewing(viewingId, tenantId);
        activityService.createViewingActivity(tenantId, viewing.getId(), tenantId, "VIEWING_CANCELLED");

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Viewing cancelled successfully!");
        response.put("viewing", Map.of(
                "id", viewing.getId(),
                "propertyId", viewing.getPropertyId(),
                "propertyName", viewing.getPropertyName(),
                "status", viewing.getStatus()
        ));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<Map<String, Object>> getViewingById(@PathVariable String id) {
        String tenantId = authUtils.getCurrentUserId();
        if (tenantId == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Unable to authenticate user"));
        }

        Viewing viewing = viewingService.getViewingById(id, tenantId);
        if (viewing == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Viewing not found"));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("viewing", Map.of(
                "id", viewing.getId(),
                "propertyId", viewing.getPropertyId(),
                "propertyName", viewing.getPropertyName(),
                "tenantId", viewing.getTenantId(),
                "viewingDate", viewing.getViewingDate().toString(),
                "status", viewing.getStatus(),
                "notes", viewing.getNotes(),
                "important", viewing.isImportant()
        ));
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<Map<String, Object>> getViewingsByTenant(
            @RequestParam String tenantId) {
        String currentTenantId = authUtils.getCurrentUserId();
        if (currentTenantId == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Unable to authenticate user"));
        }
        if (!currentTenantId.equals(tenantId)) {
            return ResponseEntity.status(403).body(Map.of("message", "Forbidden: Access denied for this tenant"));
        }

        List<Viewing> viewings = viewingService.getViewingsByTenantId(tenantId);
        Map<String, Object> response = new HashMap<>();
        response.put("viewings", viewings.stream().map(viewing -> Map.of(
                "id", viewing.getId(),
                "propertyId", viewing.getPropertyId(),
                "propertyName", viewing.getPropertyName(),
                "tenantId", viewing.getTenantId(),
                "viewingDate", viewing.getViewingDate().toString(),
                "status", viewing.getStatus(),
                "notes", viewing.getNotes(),
                "important", viewing.isImportant()
        )).toList());
        return ResponseEntity.ok(response);
    }
}