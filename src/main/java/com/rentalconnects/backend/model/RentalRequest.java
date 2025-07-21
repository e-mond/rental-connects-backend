package com.rentalconnects.backend.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Represents a rental request submitted by a tenant for a property in the RentalConnects application.
 * A rental request is a preliminary inquiry made by a tenant to express interest in renting a property.
 * This model is mapped to the "rental_requests" collection in MongoDB.
 */
@Document(collection = "rental_requests")
public class RentalRequest {

    @Id
    private String id;

    @NotBlank(message = "Tenant ID cannot be blank")
    private String tenantId;

    @NotBlank(message = "Property ID cannot be blank")
    private String propertyId;

    @NotBlank(message = "Status cannot be blank")
    private String status;

    @NotNull(message = "Created at timestamp cannot be null")
    private LocalDateTime createdAt;

    private String message; // Optional message from the tenant to the landlord

    // Default Constructor
    /**
     * Default constructor for the RentalRequest entity.
     * Required by Spring Data MongoDB for deserialization.
     */
    public RentalRequest() {
    }

    /**
     * Parameterized constructor for creating a RentalRequest object.
     *
     * @param id         The unique identifier of the rental request
     * @param tenantId   The ID of the tenant submitting the request
     * @param propertyId The ID of the property being requested
     * @param status     The status of the request (e.g., "Pending", "Approved", "Rejected")
     * @param createdAt  The timestamp when the request was created
     * @param message    An optional message from the tenant to the landlord
     */
    public RentalRequest(String id, String tenantId, String propertyId, String status,
                         LocalDateTime createdAt, String message) {
        this.id = id;
        this.tenantId = tenantId;
        this.propertyId = propertyId;
        this.status = status;
        this.createdAt = createdAt;
        this.message = message;
    }

    // Getters and Setters

    /**
     * Gets the ID of the rental request.
     *
     * @return The rental request ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of the rental request.
     *
     * @param id The rental request ID to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the ID of the tenant who submitted the request.
     *
     * @return The tenant ID
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * Sets the ID of the tenant who submitted the request.
     *
     * @param tenantId The tenant ID to set
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * Gets the ID of the property being requested.
     *
     * @return The property ID
     */
    public String getPropertyId() {
        return propertyId;
    }

    /**
     * Sets the ID of the property being requested.
     *
     * @param propertyId The property ID to set
     */
    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    /**
     * Gets the status of the rental request.
     *
     * @return The status (e.g., "Pending", "Approved", "Rejected")
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the rental request.
     *
     * @param status The status to set (e.g., "Pending", "Approved", "Rejected")
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the timestamp when the rental request was created.
     *
     * @return The creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the timestamp when the rental request was created.
     *
     * @param createdAt The creation timestamp to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the optional message from the tenant to the landlord.
     *
     * @return The message, or null if none was provided
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the optional message from the tenant to the landlord.
     *
     * @param message The message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    // Utility Methods

    /**
     * Checks if the rental request is still pending.
     *
     * @return True if the status is "Pending", false otherwise
     */
    public boolean isPending() {
        return "Pending".equalsIgnoreCase(status);
    }

    /**
     * Calculates the number of days since the rental request was created.
     *
     * @return The number of days since creation, or 0 if createdAt is null
     */
    public long getDaysSinceCreated() {
        if (createdAt == null) {
            return 0;
        }
        return createdAt.toLocalDate().until(LocalDateTime.now().toLocalDate()).getDays();
    }

    /**
     * Generates a string representation of the RentalRequest object.
     *
     * @return A string representation of the rental request
     */
    @Override
    public String toString() {
        return "RentalRequest{" +
                "id='" + id + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", propertyId='" + propertyId + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", message='" + message + '\'' +
                '}';
    }

    /**
     * Checks if this RentalRequest object is equal to another object.
     * Two RentalRequest objects are considered equal if their IDs are the same.
     *
     * @param o The object to compare with
     * @return True if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RentalRequest that = (RentalRequest) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    /**
     * Generates a hash code for the RentalRequest object based on its ID.
     *
     * @return The hash code
     */
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}