package com.rentalconnects.backend.service;

import java.util.List;

import com.rentalconnects.backend.model.RentalApplication;

/**
 * Service interface for handling rental application-related operations.
 */
public interface RentalApplicationService {

    /**
     * Retrieves all rental applications for a specific tenant.
     *
     * @param tenantId The ID of the tenant to query rental applications for
     * @return List of rental applications associated with the tenant
     */
    List<RentalApplication> getApplicationsByTenantId(String tenantId);
}