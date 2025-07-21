package com.rentalconnects.backend.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.rentalconnects.backend.model.Transaction;

/**
 * Repository for managing Transaction entities in MongoDB.
 */
@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {

    List<Transaction> findByLandlordId(String landlordId);
    List<Transaction> findByTenantId(String tenantId);
    List<Transaction> findByLandlordIdAndStatus(String landlordId, String status);
    List<Transaction> findByTenantIdAndStatus(String tenantId, String status);
}