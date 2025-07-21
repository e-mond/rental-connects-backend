package com.rentalconnects.backend.service;

import java.util.List;

import com.rentalconnects.backend.dto.LeaseDTO;
import com.rentalconnects.backend.model.Lease;

/**
 * Service interface for managing lease-related operations in the RentalConnects application.
 */
public interface LeaseService {

    /**
     * Retrieves all leases associated with a specific landlord.
     *
     * @param landlordId The ID of the landlord.
     * @return A list of Lease objects associated with the landlord.
     */
    List<Lease> getLeasesByLandlordId(String landlordId);

    /**
     * Retrieves all leases associated with a specific tenant.
     *
     * @param tenantId The ID of the tenant.
     * @return A list of LeaseDTO objects associated with the tenant.
     */
    List<LeaseDTO> getLeasesByTenantId(String tenantId);

    /**
     * Retrieves all leases for a specific landlord, formatted as DTOs.
     *
     * @param userId The ID of the landlord.
     * @return A list of LeaseDTO objects for the landlord.
     */
    List<LeaseDTO> getLeasesForLandlord(String userId);

    /**
     * Retrieves leases that are upcoming for renewal for a specific landlord.
     *
     * @param userId The ID of the landlord.
     * @return A list of LeaseDTO objects for leases with 30 or fewer days remaining.
     */
    List<LeaseDTO> getUpcomingLeaseRenewals(String userId);
}