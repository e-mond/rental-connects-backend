package com.rentalconnects.backend.service;

import com.rentalconnects.backend.model.MaintenanceRequest;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for managing maintenance requests in the RentalConnects application.
 * Defines methods for CRUD operations and scheduling maintenance requests for tenants and landlords.
 */
public interface MaintenanceService {

    /**
     * Fetches all maintenance requests for a specific tenant.
     * @param tenantId The ID of the tenant
     * @return List of maintenance requests
     */
    List<MaintenanceRequest> getMaintenanceRequestsByTenantId(String tenantId);

    /**
     * Fetches all maintenance requests for a specific landlord.
     * @param landlordId The ID of the landlord
     * @return List of maintenance requests
     */
    List<MaintenanceRequest> getMaintenanceRequestsByLandlordId(String landlordId);

    /**
     * Fetches all maintenance requests for a landlord with a specific status.
     * @param landlordId The ID of the landlord
     * @param status The status of the maintenance requests (e.g., "Open", "In Progress")
     * @return List of maintenance requests
     */
    List<MaintenanceRequest> getMaintenanceRequestsByLandlordIdAndStatus(String landlordId, String status);

    /**
     * Fetches all maintenance requests in the system.
     * @return List of all maintenance requests
     */
    List<MaintenanceRequest> getAllMaintenanceRequests();

    /**
     * Creates a new maintenance request.
     * @param request The maintenance request to create
     * @return The created maintenance request
     */
    MaintenanceRequest createMaintenanceRequest(MaintenanceRequest request);

    /**
     * Updates an existing maintenance request.
     * @param requestId The ID of the maintenance request
     * @param requestData The updated maintenance request data
     * @param tenantId The ID of the tenant
     * @return The updated maintenance request
     */
    MaintenanceRequest updateMaintenanceRequest(String requestId, MaintenanceRequest requestData, String tenantId);

    /**
     * Deletes a maintenance request.
     * @param requestId The ID of the maintenance request
     * @param tenantId The ID of the tenant
     */
    void deleteMaintenanceRequest(String requestId, String tenantId);

    /**
     * Fetches a specific maintenance request by ID for a tenant.
     * @param requestId The ID of the maintenance request
     * @param tenantId The ID of the tenant
     * @return The maintenance request, or null if not found or unauthorized
     */
    MaintenanceRequest getMaintenanceRequestById(String requestId, String tenantId);

    /**
     * Schedules a maintenance request by setting the scheduled date.
     * @param requestId The ID of the maintenance request
     * @param scheduledDate The scheduled date and time
     * @param landlordId The ID of the landlord
     * @return The updated maintenance request
     */
    MaintenanceRequest scheduleMaintenanceRequest(String requestId, LocalDateTime scheduledDate, String landlordId);

    /**
     * Submits a new maintenance request on behalf of a tenant.
     * @param tenantId The ID of the tenant submitting the request
     * @param propertyId The ID of the property for the request
     * @param description The description of the issue
     * @return The submitted maintenance request
     */
    MaintenanceRequest submitMaintenanceRequest(String tenantId, String propertyId, String description);
}
