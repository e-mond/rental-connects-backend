package com.rentalconnects.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.rentalconnects.backend.dto.PaymentDTO;
import com.rentalconnects.backend.model.Payment;

/**
 * Service interface for managing payment-related operations in the RentalConnects application.
 * Provides methods to handle payment retrieval, processing, creation, updates, and deletion
 * for tenants and landlords.
 */
public interface PaymentService {

    /**
     * Retrieves all payments for a specific tenant.
     *
     * @param tenantId The ID of the tenant.
     * @return A list of Payment objects associated with the tenant.
     */
    List<Payment> getPaymentsByTenantId(String tenantId);

    /**
     * Retrieves all payments for a specific landlord.
     *
     * @param landlordId The ID of the landlord.
     * @return A list of Payment objects associated with the landlord.
     */
    List<Payment> getPaymentsByLandlordId(String landlordId);

    /**
     * Retrieves all payments for a specific landlord as DTOs.
     *
     * @param landlordId The ID of the landlord.
     * @return A list of PaymentDTO objects associated with the landlord.
     */
    List<PaymentDTO> getPaymentsForLandlord(String landlordId);

    /**
     * Retrieves payments for a tenant with a specific status.
     *
     * @param tenantId The ID of the tenant.
     * @param status   The status of the payments (e.g., COMPLETED, PENDING).
     * @return A list of Payment objects for the tenant with the specified status.
     */
    List<Payment> getPaymentsByTenantIdAndStatus(String tenantId, String status);

    /**
     * Retrieves payments for a landlord with a specific status.
     *
     * @param landlordId The ID of the landlord.
     * @param status     The status of the payments (e.g., COMPLETED, PENDING).
     * @return A list of Payment objects for the landlord with the specified status.
     */
    List<Payment> getPaymentsByLandlordIdAndStatus(String landlordId, String status);

    /**
     * Retrieves payments for a specific lease.
     *
     * @param leaseId The ID of the lease.
     * @return A list of Payment objects associated with the lease.
     */
    List<Payment> getPaymentsByLeaseId(String leaseId);

    /**
     * Retrieves payments for a tenant within a specific date range.
     *
     * @param tenantId  The ID of the tenant.
     * @param startDate The start date and time of the range (inclusive).
     * @param endDate   The end date and time of the range (inclusive).
     * @return A list of Payment objects made by the tenant within the date range.
     */
    List<Payment> getPaymentsByTenantIdAndDateRange(String tenantId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Processes a payment for a tenant and updates its status.
     *
     * @param paymentId      The ID of the payment to process.
     * @param tenantId       The ID of the tenant making the payment.
     * @param paymentDetails Map containing payment details (e.g., amount).
     * @return The processed Payment object.
     * @throws IllegalArgumentException If payment or lease is not found or amount is invalid.
     * @throws IllegalAccessError       If the tenant is not authorized to access the payment.
     */
    Payment processPayment(String paymentId, String tenantId, Map<String, Object> paymentDetails);

    /**
     * Creates a new payment record.
     *
     * @param payment The Payment object to create.
     * @return The created Payment object.
     * @throws IllegalArgumentException If required fields are missing.
     */
    Payment createPayment(Payment payment);

    /**
     * Updates an existing payment record.
     *
     * @param payment The Payment object to update.
     * @return The updated Payment object.
     * @throws IllegalArgumentException If required fields are missing or payment is not found.
     */
    Payment updatePayment(Payment payment);

    /**
     * Deletes a payment by its ID.
     *
     * @param paymentId The ID of the payment to delete.
     * @throws IllegalArgumentException If the payment is not found.
     */
    void deletePayment(String paymentId);

    /**
     * Retrieves a payment by its ID.
     *
     * @param paymentId The ID of the payment to retrieve.
     * @return The Payment object, or null if not found.
     */
    Payment getPaymentById(String paymentId);
}