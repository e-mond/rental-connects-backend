package com.rentalconnects.backend.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Represents a rental application submitted by a tenant for a property in the RentalConnects application.
 * This model is mapped to the "rental_applications" collection in MongoDB.
 */
@Document(collection = "rental_applications")
public class RentalApplication {

    @Id
    private String id;

    @NotBlank(message = "Tenant ID cannot be blank")
    private String tenantId;

    @NotBlank(message = "User ID cannot be blank")
    private String userId;

    @NotBlank(message = "Property ID cannot be blank")
    private String propertyId;

    @NotBlank(message = "Status cannot be blank")
    private String status;

    @NotNull(message = "Submitted at cannot be null")
    private LocalDateTime submittedAt;

    @NotBlank(message = "Move-in date cannot be blank")
    private String moveInDate;

    @NotNull(message = "Number of occupants is required")
    @Min(value = 1, message = "Number of occupants must be at least 1")
    private int occupants;

    // Getters and Setters
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getMoveInDate() {
        return moveInDate;
    }

    public void setMoveInDate(String moveInDate) {
        this.moveInDate = moveInDate;
    }

    public int getOccupants() {
        return occupants;
    }

    public void setOccupants(int occupants) {
        this.occupants = occupants;
    }
}
