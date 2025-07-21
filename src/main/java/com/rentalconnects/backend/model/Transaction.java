package com.rentalconnects.backend.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Represents a transaction in the RentalConnects application.
 * Mapped to the "transactions" collection in MongoDB.
 */
@Document(collection = "transactions")
public class Transaction {

    @Id
    private String id;

    @NotNull(message = "Tenant ID cannot be null")
    private String tenantId;

    @NotNull(message = "Landlord ID cannot be null")
    private String landlordId;

    @NotNull(message = "Property ID cannot be null")
    private String propertyId;

    @NotNull(message = "Amount cannot be null")
    @Min(value = 0, message = "Amount cannot be negative")
    private Double amount;

    @NotNull(message = "Transaction date cannot be null")
    private LocalDateTime transactionDate;

    @NotNull(message = "Status cannot be null")
    private String status;

    @NotNull(message = "Type cannot be null")
    private String type; // Transaction type (e.g., "RENT", "DEPOSIT")

    private String tenantName; // Tenant name for DTO conversion
    private String propertyName; // Property name for DTO conversion

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

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }
}