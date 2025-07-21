package com.rentalconnects.backend.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.rentalconnects.backend.dto.TransactionDTO;
import com.rentalconnects.backend.model.Notification;
import com.rentalconnects.backend.model.Payment;
import com.rentalconnects.backend.model.Transaction;
import com.rentalconnects.backend.repository.PaymentRepository;
import com.rentalconnects.backend.repository.TransactionRepository;
import com.rentalconnects.backend.service.NotificationService;
import com.rentalconnects.backend.service.TransactionService;

/**
 * Implementation of the TransactionService interface.
 * Manages transaction operations, including retrieval for landlords and tenants,
 * filtering by status, and creating transactions from payments with notifications.
 */
@Service
public class TransactionServiceImpl implements TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);
    private final TransactionRepository transactionRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationService notificationService;

    /**
     * Constructor for TransactionServiceImpl with dependency injection.
     *
     * @param transactionRepository The repository for transaction persistence.
     * @param paymentRepository     The repository for payment persistence.
     * @param notificationService   The service for sending notifications.
     */
    public TransactionServiceImpl(
            TransactionRepository transactionRepository,
            PaymentRepository paymentRepository,
            NotificationService notificationService) {
        this.transactionRepository = transactionRepository;
        this.paymentRepository = paymentRepository;
        this.notificationService = notificationService;
        logger.info("TransactionServiceImpl initialized");
    }

    /**
     * Retrieves all transactions associated with a specific landlord as DTOs.
     *
     * @param userId The ID of the landlord.
     * @return A list of TransactionDTO objects for the landlord.
     */
    @Override
    public List<TransactionDTO> getTransactionsForLandlord(String userId) {
        logger.debug("Fetching transactions for landlord ID: {}", userId);
        // Retrieve transactions from repository
        List<Transaction> transactions = transactionRepository.findByLandlordId(userId);
        // Convert to DTOs
        return transactions.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Retrieves all transactions associated with a specific landlord as entities.
     *
     * @param landlordId The ID of the landlord.
     * @return A list of Transaction objects for the landlord.
     */
    @Override
    public List<Transaction> getTransactionsByLandlordId(String landlordId) {
        logger.debug("Fetching transaction entities for landlord ID: {}", landlordId);
        return transactionRepository.findByLandlordId(landlordId);
    }

    /**
     * Retrieves all transactions associated with a specific tenant as DTOs.
     *
     * @param userId The ID of the tenant.
     * @return A list of TransactionDTO objects for the tenant.
     */
    @Override
    public List<TransactionDTO> getTransactionsForTenant(String userId) {
        logger.debug("Fetching transactions for tenant ID: {}", userId);
        // Retrieve transactions from repository
        List<Transaction> transactions = transactionRepository.findByTenantId(userId);
        // Convert to DTOs
        return transactions.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Retrieves transactions for a landlord filtered by status.
     *
     * @param landlordId The ID of the landlord.
     * @param status     The status to filter by (e.g., PENDING, COMPLETED).
     * @return A list of TransactionDTO objects matching the status.
     */
    @Override
    public List<TransactionDTO> getTransactionsByLandlordIdAndStatus(String landlordId, String status) {
        logger.debug("Fetching transactions for landlord ID: {} with status: {}", landlordId, status);
        // Retrieve filtered transactions
        List<Transaction> transactions = transactionRepository.findByLandlordIdAndStatus(landlordId, status);
        // Convert to DTOs
        return transactions.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Retrieves transactions for a tenant filtered by status.
     *
     * @param tenantId The ID of the tenant.
     * @param status   The status to filter by (e.g., PENDING, COMPLETED).
     * @return A list of TransactionDTO objects matching the status.
     */
    @Override
    public List<TransactionDTO> getTransactionsByTenantIdAndStatus(String tenantId, String status) {
        logger.debug("Fetching transactions for tenant ID: {} with status: {}", tenantId, status);
        // Retrieve filtered transactions
        List<Transaction> transactions = transactionRepository.findByTenantIdAndStatus(tenantId, status);
        // Convert to DTOs
        return transactions.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Creates a transaction from a payment and sends notifications to tenant and landlord.
     *
     * @param paymentId The ID of the payment to create a transaction from.
     * @return The created TransactionDTO object.
     * @throws IllegalArgumentException If the payment is not found.
     */
    @Override
    public TransactionDTO createTransactionFromPayment(String paymentId) {
        logger.info("Creating transaction from payment ID: {}", paymentId);
        // Fetch payment from repository
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> {
                    logger.error("Payment not found with ID: {}", paymentId);
                    return new IllegalArgumentException("Payment not found: " + paymentId);
                });

        // Create new transaction
        Transaction transaction = new Transaction();
        transaction.setId(payment.getId()); // Use same ID for simplicity
        transaction.setTenantId(payment.getTenantId());
        transaction.setLandlordId(payment.getLandlordId());
        transaction.setPropertyId(payment.getLeaseId()); // Map leaseId to propertyId
        transaction.setAmount(payment.getAmount());
        transaction.setTransactionDate(payment.getPaymentDate());
        transaction.setStatus(payment.getStatus());
        transaction.setType("RENT"); // Default type; can be dynamic based on payment
        transaction.setTenantName(payment.getName());
        transaction.setPropertyName(payment.getApt());

        // Save transaction to repository
        Transaction savedTransaction = transactionRepository.save(transaction);
        logger.info("Transaction created successfully with ID: {}", savedTransaction.getId());

        // Send notification to tenant
        Notification tenantNotification = new Notification();
        tenantNotification.setRecipientId(payment.getTenantId());
        tenantNotification.setLandlordId(payment.getLandlordId());
        tenantNotification.setTenantId(payment.getTenantId());
        tenantNotification.setMessage("Transaction created for payment of $" + payment.getAmount() + " for " + payment.getName());
        tenantNotification.setIsRead(false);
        tenantNotification.setCreatedAt(LocalDateTime.now());
        notificationService.sendNotification(tenantNotification);

        // Send notification to landlord
        Notification landlordNotification = new Notification();
        landlordNotification.setRecipientId(payment.getLandlordId());
        landlordNotification.setLandlordId(payment.getLandlordId());
        landlordNotification.setTenantId(payment.getTenantId());
        landlordNotification.setMessage("Transaction created for payment of $" + payment.getAmount() + " from " + payment.getName());
        landlordNotification.setIsRead(false);
        landlordNotification.setCreatedAt(LocalDateTime.now());
        notificationService.sendNotification(landlordNotification);

        return convertToDTO(savedTransaction);
    }

    /**
     * Converts a Transaction entity to a TransactionDTO for API responses.
     *
     * @param transaction The Transaction entity to convert.
     * @return The converted TransactionDTO.
     */
    private TransactionDTO convertToDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setType(transaction.getType()); // Use dynamic type from transaction
        dto.setTenant(transaction.getTenantName());
        dto.setProperty(transaction.getPropertyName());
        dto.setAmount(transaction.getAmount());
        dto.setDate(transaction.getTransactionDate());
        return dto;
    }
}