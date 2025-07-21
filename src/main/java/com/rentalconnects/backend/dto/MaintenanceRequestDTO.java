package com.rentalconnects.backend.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for MaintenanceRequest, used to transfer data between the backend and frontend.
 * Maps to the fields expected by the frontend, including type, details, and address.
 */
public class MaintenanceRequestDTO {

    private String id;
    private String tenantId;
    private String leaseId;
    private String landlordId;
    private String type;
    private String details;
    private String address;
    private String status;
    private LocalDateTime scheduledDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors

    /**
     * Default constructor for MaintenanceRequestDTO.
     */
    public MaintenanceRequestDTO() {
    }

    // Getters and Setters

    /**
     * Gets the ID of the maintenance request.
     * @return The maintenance request ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of the maintenance request.
     * @param id The maintenance request ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the ID of the tenant who submitted the request.
     * @return The tenant ID
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * Sets the ID of the tenant who submitted the request.
     * @param tenantId The tenant ID
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * Gets the ID of the lease associated with the request.
     * @return The lease ID
     */
    public String getLeaseId() {
        return leaseId;
    }

    /**
     * Sets the ID of the lease associated with the request.
     * @param leaseId The lease ID
     */
    public void setLeaseId(String leaseId) {
        this.leaseId = leaseId;
    }

    /**
     * Gets the ID of the landlord responsible for the request.
     * @return The landlord ID
     */
    public String getLandlordId() {
        return landlordId;
    }

    /**
     * Sets the ID of the landlord responsible for the request.
     * @param landlordId The landlord ID
     */
    public void setLandlordId(String landlordId) {
        this.landlordId = landlordId;
    }

    /**
     * Gets the type of maintenance request (e.g., Plumbing, Electrical).
     * @return The request type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of maintenance request.
     * @param type The request type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the detailed description of the maintenance issue.
     * @return The description (mapped to details in frontend)
     */
    public String getDetails() {
        return details;
    }

    /**
     * Sets the detailed description of the maintenance issue.
     * @param details The description
     */
    public void setDetails(String details) {
        this.details = details;
    }

    /**
     * Gets the address of the property where maintenance is needed.
     * @return The property address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the address of the property where maintenance is needed.
     * @param address The property address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets the status of the maintenance request (e.g., Open, In Progress).
     * @return The status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the maintenance request.
     * @param status The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the scheduled date and time for the maintenance.
     * @return The scheduled date
     */
    public LocalDateTime getScheduledDate() {
        return scheduledDate;
    }

    /**
     * Sets the scheduled date and time for the maintenance.
     * @param scheduledDate The scheduled date
     */
    public void setScheduledDate(LocalDateTime scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    /**
     * Gets the creation timestamp of the maintenance request.
     * @return The creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp of the maintenance request.
     * @param createdAt The creation timestamp
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the last updated timestamp of the maintenance request.
     * @return The last updated timestamp
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the last updated timestamp of the maintenance request.
     * @param updatedAt The last updated timestamp
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}