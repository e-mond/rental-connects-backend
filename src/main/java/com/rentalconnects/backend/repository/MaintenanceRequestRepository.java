package com.rentalconnects.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.rentalconnects.backend.model.MaintenanceRequest;

/**
 * Repository interface for managing {@link MaintenanceRequest} entities in the MongoDB database.
 * Provides methods to perform CRUD operations and custom queries for maintenance request-related data.
 */
@Repository
public interface MaintenanceRequestRepository extends MongoRepository<MaintenanceRequest, String> {

    /**
     * Retrieves a list of maintenance requests submitted by a specific tenant.
     * @param tenantId The ID of the tenant
     * @return List of maintenance requests for the tenant
     */
    List<MaintenanceRequest> findByTenantId(String tenantId);

    /**
     * Retrieves a list of maintenance requests assigned to a specific landlord.
     * @param landlordId The ID of the landlord
     * @return List of maintenance requests for the landlord
     */
    List<MaintenanceRequest> findByLandlordId(String landlordId);

    /**
     * Retrieves a list of maintenance requests for a landlord with a specific status.
     * @param landlordId The ID of the landlord
     * @param status The status (e.g., "Open", "In Progress")
     * @return List of maintenance requests with the specified status
     */
    List<MaintenanceRequest> findByLandlordIdAndStatus(String landlordId, String status);

    /**
     * Retrieves a list of maintenance requests for a tenant with a specific status.
     * @param tenantId The ID of the tenant
     * @param status The status (e.g., "Open", "In Progress")
     * @return List of maintenance requests with the specified status
     */
    List<MaintenanceRequest> findByTenantIdAndStatus(String tenantId, String status);

    /**
     * Retrieves a list of maintenance requests for a specific lease.
     * @param leaseId The ID of the lease
     * @return List of maintenance requests for the lease
     */
    List<MaintenanceRequest> findByLeaseId(String leaseId);

    /**
     * Retrieves a maintenance request by ID and tenant ID for authorization.
     * @param id The maintenance request ID
     * @param tenantId The tenant ID
     * @return Optional containing the maintenance request, if found
     */
    Optional<MaintenanceRequest> findByIdAndTenantId(String id, String tenantId);

    /**
     * Retrieves a maintenance request by ID and landlord ID for authorization.
     * @param id The maintenance request ID
     * @param landlordId The landlord ID
     * @return Optional containing the maintenance request, if found
     */
    Optional<MaintenanceRequest> findByIdAndLandlordId(String id, String landlordId);

    /**
     * Retrieves a maintenance request by property ID for tenant submission.
     * @param propertyId The property ID
     * @return List of maintenance requests for the property
     */
    List<MaintenanceRequest> findByPropertyId(String propertyId);
}
