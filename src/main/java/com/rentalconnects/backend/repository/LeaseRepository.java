package com.rentalconnects.backend.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.rentalconnects.backend.model.Lease;

/**
 * Repository interface for managing {@link Lease} entities in the MongoDB database.
 * Provides methods to perform CRUD operations and custom queries for lease-related data.
 */
@Repository
public interface LeaseRepository extends MongoRepository<Lease, String> {

    /**
     * Retrieves a list of leases associated with a specific landlord.
     *
     * @param landlordId The ID of the landlord to query leases for.
     * @return List of {@link Lease} entities associated with the landlord.
     */
    List<Lease> findByLandlordId(String landlordId);

    /**
     * Retrieves a list of leases associated with a specific tenant.
     *
     * @param tenantId The ID of the tenant to query leases for.
     * @return List of {@link Lease} entities associated with the tenant.
     */
    List<Lease> findByTenantId(String tenantId);

    /**
     * Retrieves a list of leases for a specific property.
     *
     * @param propertyId The ID of the property to query leases for.
     * @return List of {@link Lease} entities associated with the property.
     */
    List<Lease> findByPropertyId(String propertyId);

    /**
     * Retrieves a list of leases with a specific status.
     * The status field is now valid due to its addition in the Lease model.
     *
     * @param status The status of the leases (e.g., "ACTIVE", "TERMINATED", "EXPIRED").
     * @return List of {@link Lease} entities with the specified status.
     */
    List<Lease> findByStatus(String status);
}