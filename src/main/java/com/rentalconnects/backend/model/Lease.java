package com.rentalconnects.backend.model;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotNull;

/**
 * Represents a lease agreement between a landlord and tenant in the RentalConnects application.
 * This model is mapped to the "leases" collection in MongoDB.
 */
@Document(collection = "leases")
public class Lease {

    @Id
    private String id;

    @NotNull(message = "Tenant ID cannot be null")
    private String tenantId;

    @NotNull(message = "Landlord ID cannot be null")
    private String landlordId;

    @NotNull(message = "Property ID cannot be null")
    private String propertyId;

    @NotNull(message = "Start date cannot be null")
    private LocalDate startDate;

    @NotNull(message = "End date cannot be null")
    private LocalDate endDate;

    @NotNull(message = "Monthly rent cannot be null")
    private Double monthlyRent;

    // Added status field to support findByStatus query in LeaseRepository
    private String status; // Represents the lease status (e.g., "ACTIVE", "TERMINATED", "EXPIRED")

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

    public String getLandlordId() {
        return landlordId;
    }

    public void setLandlordId(String landlordId) {
        this.landlordId = landlordId;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Double getMonthlyRent() {
        return monthlyRent;
    }

    public void setMonthlyRent(Double monthlyRent) {
        this.monthlyRent = monthlyRent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}