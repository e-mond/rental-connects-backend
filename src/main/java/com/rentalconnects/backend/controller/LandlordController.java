package com.rentalconnects.backend.controller;

import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.rentalconnects.backend.dto.MessageDTO;
import com.rentalconnects.backend.dto.TransactionDTO;
import com.rentalconnects.backend.dto.PaymentDTO;
import com.rentalconnects.backend.model.MaintenanceRequest;
import com.rentalconnects.backend.model.Message;
import com.rentalconnects.backend.service.LandlordService;
import com.rentalconnects.backend.service.TransactionService;
import com.rentalconnects.backend.service.PaymentService;
import com.rentalconnects.backend.service.MessageService;
import com.rentalconnects.backend.util.AuthUtils;

@RestController
@RequestMapping("/api/landlord")
public class LandlordController {

    private static final Logger logger = LoggerFactory.getLogger(LandlordController.class);

    private final MessageService messageService;
    private final TransactionService transactionService;
    private final LandlordService landlordService;
    private final PaymentService paymentService;
    private final AuthUtils authUtils;

    public LandlordController(MessageService messageService,
                              TransactionService transactionService,
                              LandlordService landlordService,
                              PaymentService paymentService,
                              AuthUtils authUtils) {
        this.messageService = messageService;
        this.transactionService = transactionService;
        this.landlordService = landlordService;
        this.paymentService = paymentService;
        this.authUtils = authUtils;
    }

    @GetMapping("/messages")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<?> getLandlordMessages(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            String landlordId = authUtils.getCurrentUserId();
            logger.info("Fetching messages for landlordId: {} at {}", landlordId, java.time.Instant.now());
            List<MessageDTO> messages = messageService.getMessagesForLandlord(landlordId);
            return ResponseEntity.ok(messages != null ? messages : Collections.emptyList());
        } catch (IllegalStateException e) {
            logger.error("Authentication error: {} at {}", e.getMessage(), java.time.Instant.now(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error fetching messages: {} at {}", e.getMessage(), java.time.Instant.now(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching messages: " + e.getMessage());
        }
    }

    @GetMapping("/maintenance")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<?> getMaintenanceRequests(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            String landlordId = authUtils.getCurrentUserId();
            logger.info("Fetching maintenance requests for landlordId: {} at {}", landlordId, java.time.Instant.now());
            List<MaintenanceRequest> requests = landlordService.getMaintenanceRequests(landlordId);
            return ResponseEntity.ok(requests != null ? requests : Collections.emptyList());
        } catch (IllegalStateException e) {
            logger.error("Authentication error: {} at {}", e.getMessage(), java.time.Instant.now(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error fetching maintenance requests: {} at {}", e.getMessage(), java.time.Instant.now(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching maintenance requests: " + e.getMessage());
        }
    }

    @GetMapping("/payments")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<?> getPayments(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            String landlordId = authUtils.getCurrentUserId();
            logger.info("Fetching payments for landlordId: {} at {}", landlordId, java.time.Instant.now());
            List<PaymentDTO> payments = paymentService.getPaymentsForLandlord(landlordId);
            return ResponseEntity.ok(payments != null ? payments : Collections.emptyList());
        } catch (IllegalStateException e) {
            logger.error("Authentication error: {} at {}", e.getMessage(), java.time.Instant.now(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error fetching payments: {} at {}", e.getMessage(), java.time.Instant.now(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching payments: " + e.getMessage());
        }
    }

    @GetMapping("/transactions/landlord")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<?> getTransactions(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            String landlordId = authUtils.getCurrentUserId();
            logger.info("Fetching transactions for landlordId: {} at {}", landlordId, java.time.Instant.now());
            List<TransactionDTO> transactions = transactionService.getTransactionsForLandlord(landlordId);
            return ResponseEntity.ok(transactions != null ? transactions : Collections.emptyList());
        } catch (IllegalStateException e) {
            logger.error("Authentication error: {} at {}", e.getMessage(), java.time.Instant.now(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error fetching transactions: {} at {}", e.getMessage(), java.time.Instant.now(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching transactions: " + e.getMessage());
        }
    }

    @PostMapping("/messages")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<?> sendMessage(@AuthenticationPrincipal UserDetails userDetails, @RequestBody Message message) {
        try {
            String landlordId = authUtils.getCurrentUserId();
            logger.info("Sending message from landlordId: {} at {}", landlordId, java.time.Instant.now());
            message.setSenderId(landlordId);
            MessageDTO createdMessage = messageService.sendMessage(message);
            return ResponseEntity.ok(createdMessage);
        } catch (IllegalStateException e) {
            logger.error("Authentication error: {} at {}", e.getMessage(), java.time.Instant.now(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error sending message: {} at {}", e.getMessage(), java.time.Instant.now(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending message: " + e.getMessage());
        }
    }

    @PutMapping("/messages/{messageId}/read")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<?> markMessageAsRead(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String messageId) {
        try {
            String landlordId = authUtils.getCurrentUserId();
            logger.info("Marking message as read for landlordId: {} and messageId: {} at {}", landlordId, messageId, java.time.Instant.now());
            Message message = messageService.getMessageById(messageId, landlordId);
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setId(message.getId());
            messageDTO.setSenderId(message.getSenderId());
            messageDTO.setRecipientId(message.getRecipientId());
            messageDTO.setPropertyId(message.getPropertyId());
            messageDTO.setEncryptedContent(message.getEncryptedContent());
            messageDTO.setEncryptedKey(message.getEncryptedKey());
            messageDTO.setReplyToId(message.getReplyToId());
            messageDTO.setCreatedAt(message.getCreatedAt());
            messageDTO.setRead(true);
            MessageDTO updatedMessage = messageService.updateMessage(messageId, messageDTO);
            return ResponseEntity.ok(updatedMessage);
        } catch (IllegalStateException e) {
            logger.error("Authentication error: {} at {}", e.getMessage(), java.time.Instant.now(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error marking message as read: {} at {}", e.getMessage(), java.time.Instant.now(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error marking message as read: " + e.getMessage());
        }
    }
}