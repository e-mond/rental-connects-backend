package com.rentalconnects.backend.service;

import java.util.List;

import com.rentalconnects.backend.dto.TransactionDTO;
import com.rentalconnects.backend.model.Transaction;

/**
 * Service interface for managing transaction-related operations in the RentalConnects application.
 * Provides methods to retrieve transactions for landlords and tenants, filter by status,
 * and create transactions from payments.
 */
public interface TransactionService {

    /**
     * Retrieves all transactions associated with a specific landlord.
     *
     * @param userId The ID of the landlord.
     * @return A list of TransactionDTO objects representing the landlord's transactions.
     */
    List<TransactionDTO> getTransactionsForLandlord(String userId);

    /**
     * Retrieves all transactions associated with a specific landlord as entities.
     *
     * @param landlordId The ID of the landlord.
     * @return A list of Transaction objects associated with the landlord.
     */
    List<Transaction> getTransactionsByLandlordId(String landlordId);

    /**
     * Retrieves all transactions associated with a specific tenant.
     *
     * @param userId The ID of the tenant.
     * @return A list of TransactionDTO objects representing the tenant's transactions.
     */
    List<TransactionDTO> getTransactionsForTenant(String userId);

    /**
     * Retrieves transactions for a landlord filtered by status.
     *
     * @param landlordId The ID of the landlord.
     * @param status     The status to filter by (e.g., PENDING, COMPLETED).
     * @return A list of TransactionDTO objects matching the specified status.
     */
    List<TransactionDTO> getTransactionsByLandlordIdAndStatus(String landlordId, String status);

    /**
     * Retrieves transactions for a tenant filtered by status.
     *
     * @param tenantId The ID of the tenant.
     * @param status   The status to filter by (e.g., PENDING, COMPLETED).
     * @return A list of TransactionDTO objects matching the specified status.
     */
    List<TransactionDTO> getTransactionsByTenantIdAndStatus(String tenantId, String status);

    /**
     * Creates a transaction from a payment and notifies relevant parties.
     *
     * @param paymentId The ID of the payment to create a transaction from.
     * @return The created TransactionDTO object.
     * @throws IllegalArgumentException If the payment is not found.
     */
    TransactionDTO createTransactionFromPayment(String paymentId);
}