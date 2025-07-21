package com.rentalconnects.backend.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.rentalconnects.backend.model.DocumentEntity;

/**
 * Repository interface for managing {@link DocumentEntity} entities in the MongoDB database.
 * Provides methods to perform CRUD operations and custom queries for document-related data.
 */
@Repository
public interface DocumentRepository extends MongoRepository<DocumentEntity, String> {

    /**
     * Retrieves a list of documents uploaded by a specific landlord.
     *
     * @param landlordId The ID of the landlord to query documents for
     * @return List of {@link DocumentEntity} entities uploaded by the landlord
     */
    List<DocumentEntity> findByLandlordId(String landlordId);
}