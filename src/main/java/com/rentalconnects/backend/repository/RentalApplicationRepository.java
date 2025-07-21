package com.rentalconnects.backend.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.rentalconnects.backend.model.RentalApplication;

/**
 * Repository interface for managing {@link RentalApplication} entities in the MongoDB database.
 * Provides methods to perform CRUD operations and custom queries for rental application-related data.
 */
@Repository
public interface RentalApplicationRepository extends MongoRepository<RentalApplication, String> {

    /**
     * Retrieves a list of rental applications submitted by a specific tenant.
     *
     * @param tenantId The ID of the tenant to query rental applications for
     * @return List of {@link RentalApplication} entities submitted by the tenant
     */
    List<RentalApplication> findByTenantId(String tenantId);

    /**
     * Retrieves a list of rental applications for a specific property.
     *
     * @param propertyId The ID of the property to query rental applications for
     * @return List of {@link RentalApplication} entities for the property
     */
    List<RentalApplication> findByPropertyId(String propertyId);

    /**
     * Retrieves a list of rental applications for a specific tenant with a given status.
     *
     * @param tenantId The ID of the tenant to query rental applications for
     * @param status The status of the rental applications (e.g., "Pending", "Approved", "Rejected")
     * @return List of {@link RentalApplication} entities for the tenant with the specified status
     */
    List<RentalApplication> findByTenantIdAndStatus(String tenantId, String status);

    /**
     * Retrieves a list of rental applications for a specific property with a given status.
     *
     * @param propertyId The ID of the property to query rental applications for
     * @param status The status of the rental applications (e.g., "Pending", "Approved", "Rejected")
     * @return List of {@link RentalApplication} entities for the property with the specified status
     */
    List<RentalApplication> findByPropertyIdAndStatus(String propertyId, String status);
}