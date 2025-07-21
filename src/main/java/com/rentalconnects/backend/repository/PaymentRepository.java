package com.rentalconnects.backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.rentalconnects.backend.model.Payment;

/**
 * Repository interface for managing {@link Payment} entities in the MongoDB database.
 * Provides methods to perform CRUD operations and custom queries for payment-related data.
 */
@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {

    /**
     * Retrieves a list of payments made by a specific tenant.
     *
     * @param tenantId The ID of the tenant to query payments for
     * @return List of {@link Payment} entities made by the tenant
     */
    List<Payment> findByTenantId(String tenantId);

    /**
     * Retrieves a list of payments received by a specific landlord.
     *
     * @param landlordId The ID of the landlord to query payments for
     * @return List of {@link Payment} entities received by the landlord
     */
    List<Payment> findByLandlordId(String landlordId);

    /**
     * Retrieves a list of payments made by a specific tenant with a given status.
     *
     * @param tenantId The ID of the tenant to query payments for
     * @param status   The status of the payments (e.g., "COMPLETED", "PENDING")
     * @return List of {@link Payment} entities for the tenant with the specified status
     */
    List<Payment> findByTenantIdAndStatus(String tenantId, String status);

    /**
     * Retrieves a list of payments received by a specific landlord with a given status.
     *
     * @param landlordId The ID of the landlord to query payments for
     * @param status     The status of the payments (e.g., "COMPLETED", "PENDING")
     * @return List of {@link Payment} entities for the landlord with the specified status
     */
    List<Payment> findByLandlordIdAndStatus(String landlordId, String status);

    /**
     * Retrieves a list of payments for a specific lease.
     *
     * @param leaseId The ID of the lease to query payments for
     * @return List of {@link Payment} entities associated with the lease
     */
    List<Payment> findByLeaseId(String leaseId);

    /**
     * Retrieves a list of payments made by a specific tenant within a date range.
     *
     * @param tenantId  The ID of the tenant to query payments for
     * @param startDate The start date and time of the range (inclusive)
     * @param endDate   The end date and time of the range (inclusive)
     * @return List of {@link Payment} entities made by the tenant within the date range
     */
    List<Payment> findByTenantIdAndPaymentDateBetween(String tenantId, LocalDateTime startDate, LocalDateTime endDate);
}