package com.rentalconnects.backend.service.impl;

import com.rentalconnects.backend.model.Activity;
import com.rentalconnects.backend.model.Viewing;
import com.rentalconnects.backend.repository.ActivityRepository;
import com.rentalconnects.backend.repository.ViewingRepository;
import com.rentalconnects.backend.service.ActivityService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;
    private final ViewingRepository viewingRepository;
    private static final AtomicInteger counter = new AtomicInteger(0);

    public ActivityServiceImpl(ActivityRepository activityRepository, ViewingRepository viewingRepository) {
        this.activityRepository = activityRepository;
        this.viewingRepository = viewingRepository;
    }

    @Override
    public void logActivity(String tenantId, String landlordId, String type, String message, String entityId, String propertyId) {
        if (tenantId == null && landlordId == null) {
            throw new IllegalArgumentException("Either tenantId or landlordId must be provided");
        }
        if (type == null || message == null || entityId == null) {
            throw new IllegalArgumentException("Required fields (type, message, entityId) cannot be null");
        }
        Activity activity = new Activity();
        activity.setTenantId(tenantId);
        activity.setLandlordId(landlordId);
        activity.setType(type);
        activity.setMessage(message);
        activity.setEntityId(entityId);
        activity.setPropertyId(propertyId);
        activity.setTimestamp(LocalDateTime.now());
        activity.setTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM d, yyyy, h:mm a")));
        activity.setId(generateId(type, LocalDateTime.now()));
        activityRepository.save(activity);
    }

    @Override
    public Activity createViewingActivity(String tenantId, String viewingId, String userId) {
        return createViewingActivity(tenantId, viewingId, userId, "SCHEDULED_VIEWING");
    }

    @Override
    public Activity createViewingActivity(String tenantId, String viewingId, String userId, String type) {
        if (tenantId == null || viewingId == null || userId == null || type == null) {
            throw new IllegalArgumentException("Required fields (tenantId, viewingId, userId, type) cannot be null");
        }
        Viewing viewing = viewingRepository.findById(viewingId)
                .orElseThrow(() -> new IllegalArgumentException("Viewing not found: " + viewingId));
        String propertyName = viewing.getPropertyName();
        String propertyId = viewing.getPropertyId();
        if (propertyName == null) {
            throw new IllegalArgumentException("Property name not set for viewing ID: " + viewingId);
        }
        String message = formatActivityMessage(type, propertyName);
        Activity activity = new Activity();
        activity.setTenantId(tenantId);
        activity.setUserId(userId);
        activity.setType(type);
        activity.setMessage(message);
        activity.setEntityId(viewingId);
        activity.setPropertyId(propertyId);
        activity.setTimestamp(LocalDateTime.now());
        activity.setTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM d, yyyy, h:mm a")));
        activity.setId(generateId(type, LocalDateTime.now()));
        return activityRepository.save(activity);
    }

    private String formatActivityMessage(String type, String propertyName) {
        return switch (type) {
            case "SCHEDULED_VIEWING" -> "Scheduled viewing for " + propertyName;
            case "VIEWING_RESCHEDULED" -> "Rescheduled viewing at " + propertyName;
            case "VIEWING_CANCELLED" -> "Cancelled viewing for " + propertyName;
            default -> "Activity for " + propertyName;
        };
    }

    private String generateId(String type, LocalDateTime timestamp) {
        String prefix = "RC";
        String typeShorthand = getTypeShorthand(type);
        String date = timestamp.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        String uniqueCode = "A" + counter.incrementAndGet();
        return String.format("%s/%s/%s/%s", prefix, typeShorthand, date, uniqueCode);
    }

    private String getTypeShorthand(String type) {
        return switch (type) {
            case "SCHEDULED_VIEWING" -> "SV";
            case "VIEWING_RESCHEDULED" -> "VR";
            case "VIEWING_CANCELLED" -> "VC";
            case "MAINTENANCE_REQUEST" -> "MREQ";
            case "MESSAGE" -> "MSG";
            case "PROFILE_UPDATE" -> "PROF";
            default -> "ACT";
        };
    }

    @Override
    public List<Activity> getRecentActivitiesForTenant(String tenantId) {
        if (tenantId == null) {
            throw new IllegalArgumentException("tenantId cannot be null");
        }
        return activityRepository.findByTenantIdOrderByTimestampDesc(tenantId);
    }

    @Override
    public List<Activity> getRecentActivitiesForLandlord(String landlordId) {
        if (landlordId == null) {
            throw new IllegalArgumentException("landlordId cannot be null");
        }
        return activityRepository.findByLandlordIdOrderByTimestampDesc(landlordId);
    }
}