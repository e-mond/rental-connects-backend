package com.rentalconnects.backend.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.stereotype.Service;

import com.rentalconnects.backend.model.MaintenanceRequest;
import com.rentalconnects.backend.model.Property;
import com.rentalconnects.backend.repository.MaintenanceRequestRepository;
import com.rentalconnects.backend.repository.PropertyRepository;
import com.rentalconnects.backend.service.DashboardService; // Added import
import com.rentalconnects.backend.service.LandlordService;  // Added import

/**
 * Implementation of LandlordService for managing landlord-related operations.
 * Provides methods to retrieve dashboard data, properties, and maintenance requests for a landlord.
 * Delegates dashboard data aggregation to DashboardService for consistency.
 */
@Service
public class LandlordServiceImpl implements LandlordService {

    private final PropertyRepository propertyRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final DashboardService dashboardService;

    /**
     * Constructor for LandlordServiceImpl with dependency injection.
     *
     * @param propertyRepository          Repository for property data.
     * @param maintenanceRequestRepository Repository for maintenance request data.
     * @param dashboardService            Service for generating dashboard data.
     */
  
    public LandlordServiceImpl(PropertyRepository propertyRepository,
                               MaintenanceRequestRepository maintenanceRequestRepository,
                               DashboardService dashboardService) {
        this.propertyRepository = propertyRepository;
        this.maintenanceRequestRepository = maintenanceRequestRepository;
        this.dashboardService = dashboardService;
    }

    /**
     * Retrieves dashboard data for a landlord by delegating to DashboardService and converting the result to a Map.
     *
     * @param landlordId The ID of the landlord.
     * @return A map containing dashboard data (e.g., total properties, revenue, etc.).
     */
    @Override
    public Map<String, Object> getDashboardData(String landlordId) {
        // Convert LandlordDashboardDataDTO to Map to match the interface expectation
        com.rentalconnects.backend.dto.LandlordDashboardDataDTO dashboardData = 
            dashboardService.getLandlordDashboardData(landlordId);
        Map<String, Object> result = new HashMap<>();
        result.put("totalProperties", dashboardData.getTotalProperties());
        result.put("activeRentals", dashboardData.getActiveRentals());
        result.put("monthlyRevenue", dashboardData.getMonthlyRevenue());
        result.put("averageRating", dashboardData.getAverageRating());
        result.put("pendingIssues", dashboardData.getPendingIssues());
        result.put("vacantProperties", dashboardData.getVacantProperties());
        result.put("underMaintenance", dashboardData.getUnderMaintenance());
        return result;
    }

    /**
     * Retrieves all maintenance requests associated with a landlord.
     *
     * @param landlordId The ID of the landlord.
     * @return A list of MaintenanceRequest objects.
     */
    @Override
    public List<MaintenanceRequest> getMaintenanceRequests(String landlordId) {
        return maintenanceRequestRepository.findByLandlordId(landlordId);
    }

    /**
     * Retrieves all properties owned by a landlord.
     *
     * @param landlordId The ID of the landlord.
     * @return A list of Property objects.
     */
    @Override
    public List<Property> getPropertiesByLandlordId(String landlordId) {
        return propertyRepository.findByLandlordId(landlordId);
    }

    /**
     * Retrieves a specific property by its ID.
     *
     * @param propertyId The ID of the property.
     * @return The Property object, or null if not found.
     */
    @Override
    public Property getPropertyById(String propertyId) {
        return propertyRepository.findById(propertyId).orElse(null);
    }

    /**
     * Creates a new property for a landlord.
     *
     * @param property The Property object to create.
     * @return The created Property object.
     */
    @Override
    public Property createProperty(Property property) {
        return propertyRepository.save(property);
    }

    /**
     * Updates an existing property.
     *
     * @param property The Property object with updated details.
     * @return The updated Property object.
     */
    @Override
    public Property updateProperty(Property property) {
        return propertyRepository.save(property);
    }

    /**
     * Deletes a property by its ID.
     *
     * @param propertyId The ID of the property to delete.
     */
    @Override
    public void deleteProperty(String propertyId) {
        propertyRepository.deleteById(propertyId);
    }
}