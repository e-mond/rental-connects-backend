package com.rentalconnects.backend.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.rentalconnects.backend.dto.PaymentDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.rentalconnects.backend.model.Lease;
import com.rentalconnects.backend.model.Payment;
import com.rentalconnects.backend.repository.LeaseRepository;
import com.rentalconnects.backend.repository.PaymentRepository;
import com.rentalconnects.backend.service.PaymentService;

/**
 * Implementation of PaymentService for managing payment operations.
 * Handles payment processing, retrieval, creation, updates, and deletion for tenants and landlords.
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private final PaymentRepository paymentRepository;
    private final LeaseRepository leaseRepository;

    /**
     * Constructor that initializes the payment and lease repositories.
     *
     * @param paymentRepository The repository for payment data access.
     * @param leaseRepository   The repository for lease data access.
     */
    public PaymentServiceImpl(PaymentRepository paymentRepository, LeaseRepository leaseRepository) {
        this.paymentRepository = paymentRepository;
        this.leaseRepository = leaseRepository;
        logger.info("PaymentServiceImpl initialized");
    }

    @Override
    public List<Payment> getPaymentsByTenantId(String tenantId) {
        logger.debug("Fetching payments for tenant ID: {}", tenantId);
        return paymentRepository.findByTenantId(tenantId);
    }

    @Override
    public List<Payment> getPaymentsByLandlordId(String landlordId) {
        logger.debug("Fetching payments for landlord ID: {}", landlordId);
        return paymentRepository.findByLandlordId(landlordId);
    }

    @Override
    public List<PaymentDTO> getPaymentsForLandlord(String landlordId) {
        logger.debug("Fetching payment DTOs for landlord ID: {}", landlordId);
        List<Payment> payments = paymentRepository.findByLandlordId(landlordId);
        return payments.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<Payment> getPaymentsByTenantIdAndStatus(String tenantId, String status) {
        logger.debug("Fetching payments for tenant ID: {} with status: {}", tenantId, status);
        return paymentRepository.findByTenantIdAndStatus(tenantId, status);
    }

    @Override
    public List<Payment> getPaymentsByLandlordIdAndStatus(String landlordId, String status) {
        logger.debug("Fetching payments for landlord ID: {} with status: {}", landlordId, status);
        return paymentRepository.findByLandlordIdAndStatus(landlordId, status);
    }

    @Override
    public List<Payment> getPaymentsByLeaseId(String leaseId) {
        logger.debug("Fetching payments for lease ID: {}", leaseId);
        return paymentRepository.findByLeaseId(leaseId);
    }

    @Override
    public List<Payment> getPaymentsByTenantIdAndDateRange(String tenantId, LocalDateTime startDate, LocalDateTime endDate) {
        logger.debug("Fetching payments for tenant ID: {} between {} and {}", tenantId, startDate, endDate);
        return paymentRepository.findByTenantIdAndPaymentDateBetween(tenantId, startDate, endDate);
    }

    @Override
    public Payment processPayment(String paymentId, String tenantId, Map<String, Object> paymentDetails) {
        logger.info("Processing payment with ID: {} for tenant ID: {}", paymentId, tenantId);

        // Fetch and validate the payment
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> {
                    logger.error("Payment not found with ID: {}", paymentId);
                    return new IllegalArgumentException("Payment not found: " + paymentId);
                });
        if (!payment.getTenantId().equals(tenantId)) {
            logger.error("Unauthorized access to payment ID: {} by tenant ID: {}", paymentId, tenantId);
            throw new IllegalAccessError("Unauthorized access to payment");
        }

        // Validate and update payment details
        if (paymentDetails.containsKey("amount")) {
            Double amount = (Double) paymentDetails.get("amount");
            if (amount == null || amount <= 0) {
                logger.error("Invalid payment amount: {}", amount);
                throw new IllegalArgumentException("Payment amount must be a positive value");
            }
            payment.setAmount(amount);
        } else {
            logger.error("Payment amount is missing in payment details");
            throw new IllegalArgumentException("Payment amount is required");
        }

        // Fetch lease to update landlordId
        Lease lease = leaseRepository.findById(payment.getLeaseId())
                .orElseThrow(() -> {
                    logger.error("Lease not found with ID: {}", payment.getLeaseId());
                    return new IllegalArgumentException("Lease not found: " + payment.getLeaseId());
                });
        payment.setLandlordId(lease.getLandlordId());

        // Update payment status and timestamps
        payment.setStatus("COMPLETED");
        payment.setPaymentDate(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        // Save updated payment
        Payment updatedPayment = paymentRepository.save(payment);
        logger.info("Payment processed successfully: {}", updatedPayment.getId());
        return updatedPayment;
    }

    /**
     * Converts a Payment entity to a PaymentDTO.
     *
     * @param payment The Payment entity to convert.
     * @return The converted PaymentDTO.
     */
    private PaymentDTO convertToDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setName(payment.getName());
        dto.setApt(payment.getApt());
        dto.setAmount(payment.getAmount());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setStatus(payment.getStatus());
        dto.setLandlordId(payment.getLandlordId());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setUpdatedAt(payment.getUpdatedAt());
        return dto;
    }

    @Override
    public Payment createPayment(Payment payment) {
        logger.info("Creating new payment for tenant ID: {}", payment.getTenantId());

        // Validate required fields
        if (payment.getTenantId() == null || payment.getLandlordId() == null || payment.getLeaseId() == null) {
            logger.error("Missing required fields: tenantId={}, landlordId={}, leaseId={}",
                    payment.getTenantId(), payment.getLandlordId(), payment.getLeaseId());
            throw new IllegalArgumentException("Tenant ID, landlord ID, and lease ID are required");
        }

        // Set default timestamps
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        payment.setPaymentDate(LocalDateTime.now());

        // Save payment to repository
        Payment createdPayment = paymentRepository.save(payment);
        logger.info("Payment created successfully with ID: {}", createdPayment.getId());
        return createdPayment;
    }

    @Override
    public Payment updatePayment(Payment payment) {
        logger.info("Updating payment with ID: {}", payment.getId());

        // Validate payment existence
        if (payment.getId() == null || !paymentRepository.existsById(payment.getId())) {
            logger.error("Payment not found with ID: {}", payment.getId());
            throw new IllegalArgumentException("Payment not found: " + payment.getId());
        }

        // Validate required fields
        if (payment.getTenantId() == null || payment.getLandlordId() == null || payment.getLeaseId() == null) {
            logger.error("Missing required fields: tenantId={}, landlordId={}, leaseId={}",
                    payment.getTenantId(), payment.getLandlordId(), payment.getLeaseId());
            throw new IllegalArgumentException("Tenant ID, landlord ID, and lease ID are required");
        }

        // Set default timestamps
        if (payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDateTime.now());
        }
        payment.setUpdatedAt(LocalDateTime.now());

        // Save updated payment
        Payment updatedPayment = paymentRepository.save(payment);
        logger.info("Payment updated successfully with ID: {}", updatedPayment.getId());
        return updatedPayment;
    }

    @Override
    public void deletePayment(String paymentId) {
        logger.info("Deleting payment with ID: {}", paymentId);
        if (!paymentRepository.existsById(paymentId)) {
            logger.error("Payment not found with ID: {}", paymentId);
            throw new IllegalArgumentException("Payment not found: " + paymentId);
        }
        paymentRepository.deleteById(paymentId);
        logger.info("Payment deleted successfully with ID: {}", paymentId);
    }

    @Override
    public Payment getPaymentById(String paymentId) {
        logger.debug("Fetching payment with ID: {}", paymentId);
        Optional<Payment> payment = paymentRepository.findById(paymentId);
        return payment.orElse(null);
    }
}