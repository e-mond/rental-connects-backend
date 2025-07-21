package com.rentalconnects.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@Document(collection = "activities")
public class Activity {
    @Id
    private String id;
    private String tenantId;
    private String landlordId;
    private String userId;
    private String type;
    private String message;
    private String entityId;
    private String propertyId;
    private LocalDateTime timestamp;
    private String time;

    private static final AtomicInteger counter = new AtomicInteger(0);

    public Activity() {
    }

    public Activity(String tenantId, String type, String message, String entityId, String propertyId) {
        this.tenantId = tenantId;
        this.type = type;
        this.message = message;
        this.entityId = entityId;
        this.propertyId = propertyId;
        this.timestamp = LocalDateTime.now();
        this.time = this.timestamp.format(DateTimeFormatter.ofPattern("MMM d, yyyy, h:mm a"));
        this.id = generateId(type, timestamp);
    }

    public Activity(String tenantId, String landlordId, String userId, String type, String message, String entityId, String propertyId) {
        this.tenantId = tenantId;
        this.landlordId = landlordId;
        this.userId = userId;
        this.type = type;
        this.message = message;
        this.entityId = entityId;
        this.propertyId = propertyId;
        this.timestamp = LocalDateTime.now();
        this.time = this.timestamp.format(DateTimeFormatter.ofPattern("MMM d, yyyy, h:mm a"));
        this.id = generateId(type, timestamp);
    }

    private String generateId(String type, LocalDateTime timestamp) {
        String prefix = "RC";
        String typeShorthand = getTypeShorthand(type);
        String date = timestamp.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        String uniqueCode = "A" + counter.incrementAndGet();
        return String.format("%s/%s/%s/%s", prefix, typeShorthand, date, uniqueCode);
    }

    private String getTypeShorthand(String type) {
        if (type == null) return "ACT";
        return switch (type.toUpperCase()) {
            case "SCHEDULED_VIEWING" -> "SV";
            case "VIEWING_RESCHEDULED" -> "VR";
            case "VIEWING_CANCELLED" -> "VC";
            case "MAINTENANCE_REQUEST" -> "MREQ";
            case "MESSAGE" -> "MSG";
            case "PROFILE_UPDATE" -> "PROF";
            default -> {
                String[] words = type.toUpperCase().split("_");
                StringBuilder shorthand = new StringBuilder();
                for (String word : words) {
                    if (!word.isEmpty()) {
                        shorthand.append(word.charAt(0));
                    }
                }
                yield shorthand.length() > 0 ? shorthand.toString() : "ACT";
            }
        };
    }

    // --- Manually added getters and setters ---

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getLandlordId() {
        return landlordId;
    }
    public void setLandlordId(String landlordId) {
        this.landlordId = landlordId;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public String getEntityId() {
        return entityId;
    }
    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getPropertyId() {
        return propertyId;
    }
    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
}