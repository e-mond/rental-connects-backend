package com.rentalconnects.backend.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) for the Transaction entity, used in API responses.
 * Includes fields relevant for client-side representation of transaction data.
 */
public class TransactionDTO {
    private String id;
    private String type; // Transaction type (e.g., "RENT", "DEPOSIT")
    private String tenant; // Tenant name
    private String property; // Property name
    private Double amount; // Transaction amount
    private LocalDateTime date; // Date and time of the transaction

    // No-args constructor
    public TransactionDTO() {
    }

    // All-args constructor
    public TransactionDTO(String id, String type, String tenant, String property, Double amount, LocalDateTime date) {
        this.id = id;
        this.type = type;
        this.tenant = tenant;
        this.property = property;
        this.amount = amount;
        this.date = date;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}