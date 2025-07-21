package com.rentalconnects.backend.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Represents a notification sent to a user in the RentalConnects application.
 * This model is mapped to the "notifications" collection in MongoDB.
 */
@Document(collection = "notifications")
public class Notification {

    @Id
    private String id;

    @NotNull(message = "Recipient ID cannot be null")
    private String recipientId;

    @NotBlank(message = "Landlord ID cannot be blank")
    private String landlordId; // Added to match repository method findByLandlordId

    @NotBlank(message = "Tenant ID cannot be blank")
    private String tenantId; // Added to match repository method findByTenantId

    @NotNull(message = "Message cannot be null")
    private String message;

    @NotNull(message = "Read status cannot be null")
    private Boolean isRead; // Added to match repository methods findByLandlordIdAndIsRead and findByTenantIdAndIsRead

    @NotNull(message = "Creation time cannot be null")
    private LocalDateTime createdAt;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}