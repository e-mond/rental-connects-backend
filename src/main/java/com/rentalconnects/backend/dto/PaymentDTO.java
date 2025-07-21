package com.rentalconnects.backend.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) for the Payment entity, used in API responses.
 * This DTO includes fields relevant for client-side representation of payment data,
 * aligned with the Payment model for consistency.
 */
public class PaymentDTO {
    private String id;
    private String name; // Name associated with the payment (e.g., tenant name)
    private String apt; // Apartment identifier for the payment
    private Double amount; // Payment amount
    private LocalDateTime paymentDate; // Date and time of the payment, aligned with Payment model
    private String status; // Payment status (e.g., "PENDING", "COMPLETED")
    private String landlordId; // ID of the landlord receiving the payment
    private LocalDateTime createdAt; // Timestamp when the payment was created
    private LocalDateTime updatedAt; // Timestamp when the payment was last updated

    // Getters and Setters

    /**
     * Gets the unique identifier of the payment.
     *
     * @return The payment ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the payment.
     *
     * @param id The payment ID to set.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the name associated with the payment.
     *
     * @return The payment name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name associated with the payment.
     *
     * @param name The payment name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the apartment identifier for the payment.
     *
     * @return The apartment identifier.
     */
    public String getApt() {
        return apt;
    }

    /**
     * Sets the apartment identifier for the payment.
     *
     * @param apt The apartment identifier to set.
     */
    public void setApt(String apt) {
        this.apt = apt;
    }

    /**
     * Gets the payment amount.
     *
     * @return The payment amount.
     */
    public Double getAmount() {
        return amount;
    }

    /**
     * Sets the payment amount.
     *
     * @param amount The payment amount to set.
     */
    public void setAmount(Double amount) {
        this.amount = amount;
    }

    /**
     * Gets the date and time of the payment.
     *
     * @return The payment date and time.
     */
    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    /**
     * Sets the date and time of the payment.
     *
     * @param paymentDate The payment date and time to set.
     */
    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    /**
     * Gets the status of the payment.
     *
     * @return The payment status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the payment.
     *
     * @param status The payment status to set.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the ID of the landlord receiving the payment.
     *
     * @return The landlord ID.
     */
    public String getLandlordId() {
        return landlordId;
    }

    /**
     * Sets the ID of the landlord receiving the payment.
     *
     * @param landlordId The landlord ID to set.
     */
    public void setLandlordId(String landlordId) {
        this.landlordId = landlordId;
    }

    /**
     * Gets the timestamp when the payment was created.
     *
     * @return The creation timestamp.
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the timestamp when the payment was created.
     *
     * @param createdAt The creation timestamp to set.
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the timestamp when the payment was last updated.
     *
     * @return The last update timestamp.
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the timestamp when the payment was last updated.
     *
     * @param updatedAt The last update timestamp to set.
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}