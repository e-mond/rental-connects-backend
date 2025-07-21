package com.rentalconnects.backend.dto;

public class NotificationDTO {
    private String id;
    private String landlordId; // Added
    private String tenantId;   // Added
    private String message;
    private Boolean isRead;    // Added
    private String createdAt;

    // No-args constructor
    public NotificationDTO() {
    }

    // All-args constructor
    public NotificationDTO(String id, String landlordId, String tenantId, String message, Boolean isRead, String createdAt) {
        this.id = id;
        this.landlordId = landlordId;
        this.tenantId = tenantId;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLandlordId() {
        return landlordId;
    }

    public void setLandlordId(String landlordId) {
        this.landlordId = landlordId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}