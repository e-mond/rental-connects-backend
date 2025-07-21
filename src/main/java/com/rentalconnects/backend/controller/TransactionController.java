package com.rentalconnects.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rentalconnects.backend.dto.TransactionDTO;
import com.rentalconnects.backend.service.TransactionService;
import com.rentalconnects.backend.util.AuthUtils;

/**
 * Controller class handling transaction-related API endpoints.
 */
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final AuthUtils authUtils;

    public TransactionController(TransactionService transactionService, AuthUtils authUtils) {
        this.transactionService = transactionService;
        this.authUtils = authUtils;
    }

    /**
     * Retrieves all transactions for the authenticated landlord.
     */
    @GetMapping("/landlord")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<List<TransactionDTO>> getLandlordTransactions() {
        String userId = authUtils.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        List<TransactionDTO> transactions = transactionService.getTransactionsForLandlord(userId);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Retrieves transactions for the authenticated landlord by status.
     */
    @GetMapping("/landlord/status/{status}")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<List<TransactionDTO>> getLandlordTransactionsByStatus(@PathVariable String status) {
        String userId = authUtils.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        List<TransactionDTO> transactions = transactionService.getTransactionsByLandlordIdAndStatus(userId, status);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Retrieves all transactions for the authenticated tenant.
     */
    @GetMapping("/tenant")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<List<TransactionDTO>> getTenantTransactions() {
        String userId = authUtils.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        List<TransactionDTO> transactions = transactionService.getTransactionsForTenant(userId);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Retrieves transactions for the authenticated tenant by status.
     */
    @GetMapping("/tenant/status/{status}")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<List<TransactionDTO>> getTenantTransactionsByStatus(@PathVariable String status) {
        String userId = authUtils.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        List<TransactionDTO> transactions = transactionService.getTransactionsByTenantIdAndStatus(userId, status);
        return ResponseEntity.ok(transactions);
    }
}