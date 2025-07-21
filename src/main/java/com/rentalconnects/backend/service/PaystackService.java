package com.rentalconnects.backend.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;

import com.rentalconnects.backend.model.Notification;
import com.rentalconnects.backend.model.Payment;
import com.rentalconnects.backend.repository.PaymentRepository;

/**
 * Service class for integrating with the Paystack payment gateway.
 * Handles payment initiation and callback processing for rental transactions.
 */
@Service
public class PaystackService {

    private static final Logger logger = LoggerFactory.getLogger(PaystackService.class);
    private final PaymentRepository paymentRepository;
    private final NotificationService notificationService;
    private final TransactionService transactionService;
    private final RestTemplate restTemplate;

    @Value("${paystack.api.url}")
    private String paystackApiUrl;

    @Value("${paystack.secret.key}")
    private String paystackSecretKey;

    /**
     * Constructs a PaystackService with required dependencies.
     *
     * @param paymentRepository Repository for payment data
     * @param notificationService Service for sending notifications
     * @param transactionService Service for transaction management
     * @param restTemplate HTTP client for API calls
     */
    public PaystackService(PaymentRepository paymentRepository, NotificationService notificationService,
                          TransactionService transactionService, RestTemplate restTemplate) {
        this.paymentRepository = paymentRepository;
        this.notificationService = notificationService;
        this.transactionService = transactionService;
        this.restTemplate = restTemplate;
        logger.info("PaystackService initialized");
    }

    /**
     * Initiates a Paystack payment session for the given payment ID and tenant.
     *
     * @param paymentId The unique identifier of the payment
     * @param tenantId The ID of the tenant initiating the payment
     * @param paymentDetails Map containing payment details (e.g., amount, email)
     * @return The authorization URL for the payment
     * @throws IllegalArgumentException If payment details are invalid
     * @throws IllegalStateException If Paystack API call fails
     */
    public String initiatePaystackPayment(String paymentId, String tenantId, Map<String, Object> paymentDetails) {
        logger.info("Initiating Paystack payment for paymentId: {}, tenantId: {}", paymentId, tenantId);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));

        if (!payment.getTenantId().equals(tenantId)) {
            logger.error("Unauthorized access to paymentId: {} by tenantId: {}", paymentId, tenantId);
            throw new IllegalAccessError("Unauthorized access to payment");
        }

        Double amount = (Double) paymentDetails.get("amount");
        String email = (String) paymentDetails.get("email");
        if (amount == null || amount <= 0 || email == null) {
            logger.error("Invalid payment details: amount={}, email={}", amount, email);
            throw new IllegalArgumentException("Amount and email are required");
        }

        String reference = "PAY-" + java.util.UUID.randomUUID().toString();
        Map<String, Object> paystackRequest = Map.of(
                "email", email,
                "amount", (long) (amount * 100), // Convert to kobo (Paystack uses subunit)
                "reference", reference,
                "callback_url", "http://localhost:8080/api/payments/paystack/callback",
                "metadata", Map.of("payment_id", paymentId)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + paystackSecretKey);
        headers.set("Content-Type", "application/json");

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(paystackRequest, headers);
        logger.debug("Sending Paystack request: {}", paystackRequest);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    paystackApiUrl + "/transaction/initialize",
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<Map<String, Object>>() {} // Explicitly specify the type
            );

            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null || !responseBody.containsKey("data") || !responseBody.containsKey("status")) {
                logger.error("Invalid response from Paystack: {}", responseBody);
                throw new IllegalStateException("Failed to initiate Paystack payment");
            }

            if (!"success".equals(responseBody.get("status"))) {
                logger.error("Paystack initialization failed: {}", responseBody.get("message"));
                throw new IllegalStateException("Paystack payment initialization failed");
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
            String authorizationUrl = (String) data.get("authorization_url");
            if (authorizationUrl == null) {
                logger.error("No authorization URL returned from Paystack: {}", data);
                throw new IllegalStateException("No redirect URL provided by Paystack");
            }

            payment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(payment);

            logger.info("Paystack payment initiated with authorizationUrl: {}", authorizationUrl);
            return authorizationUrl;

        } catch (HttpClientErrorException e) {
            logger.error("HTTP error while initiating Paystack payment: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new IllegalStateException("Failed to initiate Paystack payment: " + e.getMessage());
        } catch (RestClientException e) {
            logger.error("Error communicating with Paystack API: {}", e.getMessage(), e);
            throw new IllegalStateException("Failed to initiate Paystack payment: " + e.getMessage());
        }
    }

    /**
     * Processes a Paystack payment callback to update payment status and send notifications.
     *
     * @param callbackData Map containing callback data from Paystack
     */
    public void processPaystackCallback(Map<String, Object> callbackData) {
        logger.info("Processing Paystack callback: {}", callbackData);
        String status = (String) callbackData.get("status");
        @SuppressWarnings("unchecked")
        Map<String, Object> metadata = (Map<String, Object>) callbackData.get("metadata");
        String paymentId = metadata != null ? (String) metadata.get("payment_id") : null;

        if (paymentId == null) {
            logger.error("Invalid paymentId in callback: {}", callbackData);
            return;
        }

        Payment payment = paymentRepository.findById(paymentId).orElse(null);
        if (payment == null) {
            logger.error("Payment not found for paymentId: {}", paymentId);
            return;
        }

        if ("success".equalsIgnoreCase(status)) {
            payment.setStatus("COMPLETED");
            payment.setPaymentDate(LocalDateTime.now());
            payment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(payment);
            transactionService.createTransactionFromPayment(paymentId);

            Notification landlordNotification = new Notification();
            landlordNotification.setRecipientId(payment.getLandlordId());
            landlordNotification.setLandlordId(payment.getLandlordId());
            landlordNotification.setTenantId(payment.getTenantId());
            landlordNotification.setMessage("Payment of $" + payment.getAmount() + " received from tenant for " + payment.getName());
            landlordNotification.setIsRead(false);
            landlordNotification.setCreatedAt(LocalDateTime.now());
            notificationService.sendNotification(landlordNotification);

            Notification tenantNotification = new Notification();
            tenantNotification.setRecipientId(payment.getTenantId());
            tenantNotification.setLandlordId(payment.getLandlordId());
            tenantNotification.setTenantId(payment.getTenantId());
            tenantNotification.setMessage("Payment of $" + payment.getAmount() + " successfully processed for " + payment.getName());
            tenantNotification.setIsRead(false);
            tenantNotification.setCreatedAt(LocalDateTime.now());
            notificationService.sendNotification(tenantNotification);

            logger.info("Payment {} marked as COMPLETED", paymentId);
        } else {
            payment.setStatus("FAILED");
            payment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(payment);

            Notification tenantNotification = new Notification();
            tenantNotification.setRecipientId(payment.getTenantId());
            tenantNotification.setLandlordId(payment.getLandlordId());
            tenantNotification.setTenantId(payment.getTenantId());
            tenantNotification.setMessage("Payment of $" + payment.getAmount() + " for " + payment.getName() + " failed");
            tenantNotification.setIsRead(false);
            tenantNotification.setCreatedAt(LocalDateTime.now());
            notificationService.sendNotification(tenantNotification);

            logger.warn("Payment {} marked as FAILED", paymentId);
        }
    }
}