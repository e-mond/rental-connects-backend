package com.rentalconnects.backend.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.rentalconnects.backend.model.RentalRequest;

/**
 * Repository interface for managing {@link RentalRequest} entities in the MongoDB database.
 * Provides methods to perform CRUD operations and custom queries for rental request-related data.
 */
@Repository
public interface RentalRequestRepository extends MongoRepository<RentalRequest, String> {

    /**
     * Retrieves a list of rental requests submitted by a specific tenant.
     *
     * @param tenantId The ID of the tenant to query rental requests for
     * @return List of {@link RentalRequest} entities submitted by the tenant
     */
    List<RentalRequest> findByTenantId(String tenantId);

    /**
     * Retrieves a list of rental requests for a specific property.
     *
     * @param propertyId The ID of the property to query rental requests for
     * @return List of {@link RentalRequest} entities for the property
     */
    List<RentalRequest> findByPropertyId(String propertyId);

    /**
     * Retrieves a list of rental requests for a specific tenant with a given status.
     *
     * @param tenantId The ID of the tenant to query rental requests for
     * @param status The status of the rental requests (e.g., "Pending", "Approved", "Rejected")
     * @return List of {@link RentalRequest} entities for the tenant with the specified status
     */
    List<RentalRequest> findByTenantIdAndStatus(String tenantId, String status);

    /**
     * Retrieves a list of rental requests for a specific property with a given status.
     *
     * @param propertyId The ID of the property to query rental requests for
     * @param status The status of the rental requests (e.g., "Pending", "Approved", "Rejected")
     * @return List of {@link RentalRequest} entities for the property with the specified status
     */
    List<RentalRequest> findByPropertyIdAndStatus(String propertyId, String status);
}