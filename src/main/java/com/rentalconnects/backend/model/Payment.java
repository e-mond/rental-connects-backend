package com.rentalconnects.backend.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Represents a payment transaction in the RentalConnects application.
 * This model is mapped to the "payments" collection in MongoDB.
 * It tracks payment details for both tenants and landlords, including status and timestamps.
 */
@Document(collection = "payments")
public class Payment {

    @Id
    private String id;

    @NotBlank(message = "Tenant ID cannot be blank")
    private String tenantId;

    @NotBlank(message = "Landlord ID cannot be blank")
    private String landlordId;

    @NotBlank(message = "Lease ID cannot be blank")
    private String leaseId;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    private Double amount;

    private String name; // Name associated with the payment (e.g., tenant name)

    private String apt; // Apartment identifier for the payment

    @NotNull(message = "Payment date cannot be null")
    private LocalDateTime paymentDate; // Date and time of the payment, renamed from 'date' for consistency

    @NotBlank(message = "Status cannot be blank")
    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

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
     * Gets the ID of the tenant who made the payment.
     *
     * @return The tenant ID.
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * Sets the ID of the tenant who made the payment.
     *
     * @param tenantId The tenant ID to set.
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * Gets the ID of the landlord who received the payment.
     *
     * @return The landlord ID.
     */
    public String getLandlordId() {
        return landlordId;
    }

    /**
     * Sets the ID of the landlord who received the payment.
     *
     * @param landlordId The landlord ID to set.
     */
    public void setLandlordId(String landlordId) {
        this.landlordId = landlordId;
    }

    /**
     * Gets the ID of the lease associated with the payment.
     *
     * @return The lease ID.
     */
    public String getLeaseId() {
        return leaseId;
    }

    /**
     * Sets the ID of the lease associated with the payment.
     *
     * @param leaseId The lease ID to set.
     */
    public void setLeaseId(String leaseId) {
        this.leaseId = leaseId;
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
     * Gets the timestamp when the payment record was created.
     *
     * @return The creation timestamp.
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the timestamp when the payment record was created.
     *
     * @param createdAt The creation timestamp to set.
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the timestamp when the payment record was last updated.
     *
     * @return The last update timestamp.
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the timestamp when the payment record was last updated.
     *
     * @param updatedAt The last update timestamp to set.
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}