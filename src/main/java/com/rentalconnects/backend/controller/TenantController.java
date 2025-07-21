package com.rentalconnects.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rentalconnects.backend.model.Message;
import com.rentalconnects.backend.model.Payment;
import com.rentalconnects.backend.model.Property;
import com.rentalconnects.backend.model.RentalApplication;
import com.rentalconnects.backend.service.LeaseService;
import com.rentalconnects.backend.service.MessageService;
import com.rentalconnects.backend.service.PaymentService;
import com.rentalconnects.backend.service.PropertyService;
import com.rentalconnects.backend.service.RentalApplicationService;
import com.rentalconnects.backend.util.AuthUtils;

@RestController
@RequestMapping("/api")
public class TenantController {

    private final LeaseService leaseService;
    private final PaymentService paymentService;
    private final MessageService messageService;
    private final RentalApplicationService rentalApplicationService;
    private final PropertyService propertyService;
    private final AuthUtils authUtils;

    public TenantController(LeaseService leaseService,
                            PaymentService paymentService,
                            MessageService messageService,
                            RentalApplicationService rentalApplicationService,
                            PropertyService propertyService,
                            AuthUtils authUtils) {
        this.leaseService = leaseService;
        this.paymentService = paymentService;
        this.messageService = messageService;
        this.rentalApplicationService = rentalApplicationService;
        this.propertyService = propertyService;
        this.authUtils = authUtils;
    }

    @GetMapping("/messages/tenant/me")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<List<com.rentalconnects.backend.dto.MessageDTO>> fetchMessages() {
        String tenantId = authUtils.getCurrentUserId();
        if (tenantId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        List<com.rentalconnects.backend.dto.MessageDTO> messages = messageService.getMessagesByTenantId(tenantId);
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @GetMapping("/messages/tenant/{messageId}")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<com.rentalconnects.backend.dto.MessageDTO> fetchMessageById(@PathVariable String messageId) {
        String tenantId = authUtils.getCurrentUserId();
        if (tenantId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        Message message = messageService.getMessageById(messageId, tenantId);
        if (message == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        com.rentalconnects.backend.dto.MessageDTO dto = new com.rentalconnects.backend.dto.MessageDTO();
        dto.setId(message.getId());
        dto.setSenderId(message.getSenderId());
        dto.setRecipientId(message.getRecipientId());
        dto.setPropertyId(message.getPropertyId());
        dto.setEncryptedContent(message.getEncryptedContent());
        dto.setReplyToId(message.getReplyToId());
        dto.setCreatedAt(message.getCreatedAt());
        dto.setRead(message.isRead());

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/messages/tenant/send")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<com.rentalconnects.backend.dto.MessageDTO> sendMessage(@RequestBody Message messageData) {
        String tenantId = authUtils.getCurrentUserId();
        if (tenantId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        messageData.setSenderId(tenantId);
        com.rentalconnects.backend.dto.MessageDTO sentMessage = messageService.sendMessage(messageData);
        return ResponseEntity.status(HttpStatus.CREATED).body(sentMessage);
    }

    @GetMapping("/applications/tenant")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<List<RentalApplication>> getApplicationsForTenant() {
        String tenantId = authUtils.getCurrentUserId();
        if (tenantId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        List<RentalApplication> applications = rentalApplicationService.getApplicationsByTenantId(tenantId);
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/tenant/property/{propertyId}")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<Property> getPropertyById(@PathVariable String propertyId) {
        String tenantId = authUtils.getCurrentUserId();
        if (tenantId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        if (propertyId == null || propertyId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        Property property = propertyService.findById(propertyId);
        if (property == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(property);
    }

    @GetMapping("/tenant/leases")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<List<com.rentalconnects.backend.dto.LeaseDTO>> getLeases() {
        String tenantId = authUtils.getCurrentUserId();
        if (tenantId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        List<com.rentalconnects.backend.dto.LeaseDTO> leases = leaseService.getLeasesByTenantId(tenantId);
        return ResponseEntity.ok(leases);
    }

    @GetMapping("/tenant/payments")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<List<Payment>> getTenantPayments() {
        String tenantId = authUtils.getCurrentUserId();
        if (tenantId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        List<Payment> payments = paymentService.getPaymentsByTenantId(tenantId);
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    @PostMapping("/tenant/payments/{paymentId}/process")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<Payment> processPayment(@PathVariable String paymentId,
                                                  @RequestBody Map<String, Object> paymentDetails) {
        String tenantId = authUtils.getCurrentUserId();
        if (tenantId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        Payment payment = paymentService.processPayment(paymentId, tenantId, paymentDetails);
        if (payment != null && payment.getPaymentDate() != null) {
            return new ResponseEntity<>(payment, HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }
}