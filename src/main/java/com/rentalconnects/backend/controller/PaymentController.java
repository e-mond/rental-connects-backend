package com.rentalconnects.backend.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.rentalconnects.backend.model.Notification;
import com.rentalconnects.backend.model.Payment;
import com.rentalconnects.backend.service.NotificationService;
import com.rentalconnects.backend.service.PaymentService;
import com.rentalconnects.backend.service.PaystackService;
import com.rentalconnects.backend.service.TransactionService;
import com.rentalconnects.backend.util.AuthUtils;

@RestController
@RequestMapping("/api")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;
    private final NotificationService notificationService;
    private final TransactionService transactionService;
    private final PaystackService paystackService;
    private final AuthUtils authUtils;

    public PaymentController(
            PaymentService paymentService,
            NotificationService notificationService,
            TransactionService transactionService,
            PaystackService paystackService,
            AuthUtils authUtils) {
        this.paymentService = paymentService;
        this.notificationService = notificationService;
        this.transactionService = transactionService;
        this.paystackService = paystackService;
        this.authUtils = authUtils;
        logger.info("PaymentController initialized for rentalconnects");
    }

    @GetMapping("/landlord/payments/all")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<List<Payment>> getLandlordPayments(@AuthenticationPrincipal UserDetails userDetails) {
        logger.debug("processing get /api/landlord/payments/all for user: {}", userDetails.getUsername());
        String landlordId = authUtils.getCurrentUserId();
        if (landlordId == null) {
            logger.warn("no landlord id found for user: {}", userDetails.getUsername());
            return ResponseEntity.badRequest().build();
        }
        List<Payment> payments = paymentService.getPaymentsByLandlordId(landlordId);
        logger.debug("returning {} payments for landlord: {}", payments.size(), landlordId);
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    @GetMapping("/payments/tenant/{tenantId}")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<List<Payment>> getTenantPayments(
            @PathVariable String tenantId,
            @AuthenticationPrincipal UserDetails userDetails) {
        logger.debug("processing get /api/payments/tenant/{} for user: {}", tenantId, userDetails.getUsername());
        String userId = authUtils.getCurrentUserId();
        if (userId == null) {
            logger.warn("no user id found for user: {}", userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        String effectiveTenantId = "me".equalsIgnoreCase(tenantId) ? userId : tenantId;
        if (!userId.equals(effectiveTenantId)) {
            logger.warn("unauthorized access by user: {} for tenant: {}", userDetails.getUsername(), tenantId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<Payment> payments = paymentService.getPaymentsByTenantId(effectiveTenantId);
        logger.debug("returning {} payments for tenant: {}", payments.size(), effectiveTenantId);
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    @GetMapping("/payments/landlord/{landlordId}")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<List<Payment>> getLandlordPaymentsById(
            @PathVariable String landlordId,
            @AuthenticationPrincipal UserDetails userDetails) {
        logger.debug("processing get /api/payments/landlord/{} for user: {}", landlordId, userDetails.getUsername());
        String userId = authUtils.getCurrentUserId();
        if (userId == null || !userId.equals(landlordId)) {
            logger.warn("unauthorized access by user: {} for landlord: {}", userDetails.getUsername(), landlordId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<Payment> payments = paymentService.getPaymentsByLandlordId(landlordId);
        logger.debug("returning {} payments for landlord: {}", payments.size(), landlordId);
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    @PostMapping("/payments/tenant")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<Payment> createTenantPayment(
            @RequestBody Payment payment,
            @AuthenticationPrincipal UserDetails userDetails) {
        logger.debug("processing post /api/payments/tenant for user: {}", userDetails.getUsername());
        String tenantId = authUtils.getCurrentUserId();
        if (tenantId == null) {
            logger.warn("no tenant id found for user: {}", userDetails.getUsername());
            return ResponseEntity.badRequest().build();
        }
        payment.setTenantId(tenantId);
        payment.setStatus("PENDING");
        Payment createdPayment = paymentService.createPayment(payment);
        logger.info("created payment {} for tenant: {}", createdPayment.getId(), tenantId);

        Notification landlordNotification = new Notification();
        landlordNotification.setRecipientId(createdPayment.getLandlordId());
        landlordNotification.setLandlordId(createdPayment.getLandlordId());
        landlordNotification.setTenantId(tenantId);
        landlordNotification.setMessage("new payment initiated: $" + createdPayment.getAmount() + " for " + createdPayment.getName());
        landlordNotification.setIsRead(false);
        landlordNotification.setCreatedAt(LocalDateTime.now());
        notificationService.sendNotification(landlordNotification);

        Notification tenantNotification = new Notification();
        tenantNotification.setRecipientId(tenantId);
        tenantNotification.setLandlordId(createdPayment.getLandlordId());
        tenantNotification.setTenantId(tenantId);
        tenantNotification.setMessage("payment of $" + createdPayment.getAmount() + " initiated for " + createdPayment.getName());
        tenantNotification.setIsRead(false);
        tenantNotification.setCreatedAt(LocalDateTime.now());
        notificationService.sendNotification(tenantNotification);

        return new ResponseEntity<>(createdPayment, HttpStatus.CREATED);
    }

    @PostMapping("/payments/landlord")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<Payment> createPayment(
            @RequestBody Payment payment,
            @AuthenticationPrincipal UserDetails userDetails) {
        logger.debug("processing post /api/payments/landlord for user: {}", userDetails.getUsername());
        String landlordId = authUtils.getCurrentUserId();
        if (landlordId == null) {
            logger.warn("no landlord id found for user: {}", userDetails.getUsername());
            return ResponseEntity.badRequest().build();
        }
        payment.setLandlordId(landlordId);
        Payment createdPayment = paymentService.createPayment(payment);
        logger.info("created payment {} for landlord: {}", createdPayment.getId(), landlordId);

        Notification tenantNotification = new Notification();
        tenantNotification.setRecipientId(payment.getTenantId());
        tenantNotification.setLandlordId(landlordId);
        tenantNotification.setTenantId(payment.getTenantId());
        tenantNotification.setMessage("new payment recorded: $" + createdPayment.getAmount() + " for " + createdPayment.getName());
        tenantNotification.setIsRead(false);
        tenantNotification.setCreatedAt(LocalDateTime.now());
        notificationService.sendNotification(tenantNotification);

        return new ResponseEntity<>(createdPayment, HttpStatus.CREATED);
    }

    @PutMapping("/payments/landlord/{paymentId}")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<Payment> updatePayment(
            @PathVariable String paymentId,
            @RequestBody Payment payment,
            @AuthenticationPrincipal UserDetails userDetails) {
        logger.debug("processing put /api/payments/landlord/{} for user: {}", paymentId, userDetails.getUsername());
        String landlordId = authUtils.getCurrentUserId();
        if (landlordId == null) {
            logger.warn("no landlord id found for user: {}", userDetails.getUsername());
            return ResponseEntity.badRequest().build();
        }
        payment.setId(paymentId);
        payment.setLandlordId(landlordId);
        Payment updatedPayment = paymentService.updatePayment(payment);
        logger.info("updated payment {} for landlord: {}", paymentId, landlordId);

        Notification tenantNotification = new Notification();
        tenantNotification.setRecipientId(payment.getTenantId());
        tenantNotification.setLandlordId(landlordId);
        tenantNotification.setTenantId(payment.getTenantId());
        tenantNotification.setMessage("payment updated to status: " + updatedPayment.getStatus() + " for " + updatedPayment.getName());
        tenantNotification.setIsRead(false);
        tenantNotification.setCreatedAt(LocalDateTime.now());
        notificationService.sendNotification(tenantNotification);

        return new ResponseEntity<>(updatedPayment, HttpStatus.OK);
    }

    @DeleteMapping("/payments/landlord/{paymentId}")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<Void> deletePayment(
            @PathVariable String paymentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        logger.debug("processing delete /api/payments/landlord/{} for user: {}", paymentId, userDetails.getUsername());
        String landlordId = authUtils.getCurrentUserId();
        if (landlordId == null) {
            logger.warn("no landlord id found for user: {}", userDetails.getUsername());
            return ResponseEntity.badRequest().build();
        }
        Payment payment = paymentService.getPaymentById(paymentId);
        if (payment == null || !payment.getLandlordId().equals(landlordId)) {
            logger.warn("payment {} not found or not owned by landlord: {}", paymentId, landlordId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        paymentService.deletePayment(paymentId);
        logger.info("deleted payment {} for landlord: {}", paymentId, landlordId);

        Notification tenantNotification = new Notification();
        tenantNotification.setRecipientId(payment.getTenantId());
        tenantNotification.setLandlordId(landlordId);
        tenantNotification.setTenantId(payment.getTenantId());
        tenantNotification.setMessage("payment of $" + payment.getAmount() + " for " + payment.getName() + " has been deleted");
        tenantNotification.setIsRead(false);
        tenantNotification.setCreatedAt(LocalDateTime.now());
        notificationService.sendNotification(tenantNotification);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/payments/tenant/{paymentId}")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<Payment> processTenantPayment(
            @PathVariable String paymentId,
            @RequestParam String tenantId,
            @RequestBody Map<String, Object> paymentDetails,
            @AuthenticationPrincipal UserDetails userDetails) {
        logger.debug("processing post /api/payments/tenant/{} for user: {}", paymentId, userDetails.getUsername());
        String userId = authUtils.getCurrentUserId();
        if (userId == null || !userId.equals(tenantId)) {
            logger.warn("unauthorized access by user: {} for tenant: {}", userDetails.getUsername(), tenantId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        logger.info("processing payment for tenant: {}, payment: {}, method: {}", 
            tenantId, paymentId, paymentDetails.get("paymentMethod"));

        String paymentMethod = (String) paymentDetails.get("paymentMethod");
        if ("paystack".equalsIgnoreCase(paymentMethod)) {
            String redirectUrl = paystackService.initiatePaystackPayment(paymentId, tenantId, paymentDetails);
            Payment payment = paymentService.getPaymentById(paymentId);
            logger.info("paystack payment initiated for payment: {}, redirect: {}", paymentId, redirectUrl);
            return ResponseEntity.ok().header("X-Paystack-Redirect", redirectUrl).body(payment);
        } else {
            Payment processedPayment = paymentService.processPayment(paymentId, tenantId, paymentDetails);
            transactionService.createTransactionFromPayment(paymentId);
            logger.info("processed payment {} for tenant: {}", paymentId, tenantId);

            Notification landlordNotification = new Notification();
            landlordNotification.setRecipientId(processedPayment.getLandlordId());
            landlordNotification.setLandlordId(processedPayment.getLandlordId());
            landlordNotification.setTenantId(processedPayment.getTenantId());
            landlordNotification.setMessage("payment of $" + processedPayment.getAmount() + " received from tenant for " + processedPayment.getName());
            landlordNotification.setIsRead(false);
            landlordNotification.setCreatedAt(LocalDateTime.now());
            notificationService.sendNotification(landlordNotification);

            Notification tenantNotification = new Notification();
            tenantNotification.setRecipientId(processedPayment.getTenantId());
            tenantNotification.setLandlordId(processedPayment.getLandlordId());
            tenantNotification.setTenantId(processedPayment.getTenantId());
            tenantNotification.setMessage("payment of $" + processedPayment.getAmount() + " successfully processed for " + processedPayment.getName());
            tenantNotification.setIsRead(false);
            tenantNotification.setCreatedAt(LocalDateTime.now());
            notificationService.sendNotification(tenantNotification);

            return new ResponseEntity<>(processedPayment, HttpStatus.OK);
        }
    }

    @PostMapping("/payments/tenant/paystack/{paymentId}")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<Map<String, String>> initiatePaystackPayment(
            @PathVariable String paymentId,
            @RequestParam String tenantId,
            @RequestBody Map<String, Object> paymentDetails,
            @AuthenticationPrincipal UserDetails userDetails) {
        logger.debug("processing post /api/payments/tenant/paystack/{} for user: {}", paymentId, userDetails.getUsername());
        String userId = authUtils.getCurrentUserId();
        if (userId == null || !userId.equals(tenantId)) {
            logger.warn("unauthorized access by user: {} for tenant: {}", userDetails.getUsername(), tenantId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        logger.info("initiating paystack payment for tenant: {}, payment: {}", tenantId, paymentId);
        String redirectUrl = paystackService.initiatePaystackPayment(paymentId, tenantId, paymentDetails);
        return ResponseEntity.ok(Map.of("redirectUrl", redirectUrl));
    }

    @PostMapping("/payments/paystack/callback")
    public ResponseEntity<Void> handlePaystackCallback(@RequestBody Map<String, Object> callbackData) {
        logger.info("processing paystack callback: {}", callbackData);
        paystackService.processPaystackCallback(callbackData);
        return ResponseEntity.ok().build();
    }
}