package com.rentalconnects.backend.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Represents a maintenance request submitted by a tenant in the RentalConnects application.
 * This model is mapped to the "maintenance_requests" collection in MongoDB.
 */
@Document(collection = "maintenance_requests")
public class MaintenanceRequest {

    @Id
    private String id;

    @NotBlank(message = "Tenant ID cannot be blank")
    private String tenantId;

    @NotBlank(message = "User ID cannot be blank")
    private String userId;

    @NotBlank(message = "Property ID cannot be blank")
    private String propertyId;

    @NotBlank(message = "Lease ID cannot be blank")
    private String leaseId;

    @NotBlank(message = "Landlord ID cannot be blank")
    private String landlordId;

    @NotBlank(message = "Type cannot be blank")
    private String type;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @NotBlank(message = "Address cannot be blank")
    private String address;

    @NotBlank(message = "Status cannot be blank")
    private String status;

    private LocalDateTime scheduledDate;

    @NotNull(message = "Created at cannot be null")
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

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

    public String getLeaseId() {
        return leaseId;
    }

    public void setLeaseId(String leaseId) {
        this.leaseId = leaseId;
    }

    public String getLandlordId() {
        return landlordId;
    }

    public void setLandlordId(String landlordId) {
        this.landlordId = landlordId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDateTime scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
