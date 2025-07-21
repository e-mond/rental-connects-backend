package com.rentalconnects.backend.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.rentalconnects.backend.model.Viewing;

/**
 * Repository interface for managing {@link Viewing} entities in the MongoDB database.
 * Provides methods to perform CRUD operations and custom queries for viewing-related data.
 */
@Repository
public interface ViewingRepository extends MongoRepository<Viewing, String> {

    /**
     * Retrieves a list of viewings associated with a specific property.
     */
    List<Viewing> findByPropertyId(String propertyId);

    /**
     * Retrieves a list of viewings scheduled by a specific tenant.
     */
    List<Viewing> findByTenantId(String tenantId);

    /**
     * Retrieves a list of viewings for a specific property that are scheduled for a future date/time.
     */
    List<Viewing> findByPropertyIdAndViewingDateAfter(String propertyId, LocalDateTime currentDateTime);

    /**
     * Retrieves a list of viewings for a specific tenant with a given status.
     */
    List<Viewing> findByTenantIdAndStatus(String tenantId, String status);

    /**
     * Retrieves a list of viewings for a specific property with a given status.
     */
    List<Viewing> findByPropertyIdAndStatus(String propertyId, String status);

    /**
     * Retrieves a viewing by its ID.
     */
    Optional<Viewing> findById(String id);
}