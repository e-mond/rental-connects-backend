package com.rentalconnects.backend.service.impl;

import com.rentalconnects.backend.model.MaintenanceRequest;
import com.rentalconnects.backend.repository.MaintenanceRequestRepository;
import com.rentalconnects.backend.service.MaintenanceService;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
// import java.util.Optional;

/**
 * Implementation of the MaintenanceService interface.
 * Handles business logic for managing maintenance requests, including CRUD operations and authorization checks.
 */
@Service
public class MaintenanceServiceImpl implements MaintenanceService {

    private final MaintenanceRequestRepository repository;

    public MaintenanceServiceImpl(MaintenanceRequestRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<MaintenanceRequest> getMaintenanceRequestsByTenantId(String tenantId) {
        return repository.findByTenantId(tenantId);
    }

    @Override
    public List<MaintenanceRequest> getMaintenanceRequestsByLandlordId(String landlordId) {
        return repository.findByLandlordId(landlordId);
    }

    @Override
    public List<MaintenanceRequest> getMaintenanceRequestsByLandlordIdAndStatus(String landlordId, String status) {
        return repository.findByLandlordIdAndStatus(landlordId, status);
    }

    @Override
    public List<MaintenanceRequest> getAllMaintenanceRequests() {
        return repository.findAll();
    }

    @Override
    public MaintenanceRequest createMaintenanceRequest(MaintenanceRequest request) {
        validateRequest(request);
        request.setStatus("Open");
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());
        return repository.save(request);
    }

    @Override
    public MaintenanceRequest submitMaintenanceRequest(String tenantId, String propertyId, String description) {
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("Tenant ID is required");
        }
        if (propertyId == null || propertyId.isBlank()) {
            throw new IllegalArgumentException("Property ID is required");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description is required");
        }

        MaintenanceRequest request = new MaintenanceRequest();
        request.setTenantId(tenantId);
        request.setPropertyId(propertyId);
        request.setDescription(description);
        request.setStatus("Open");
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());
        return repository.save(request);
    }

    @Override
    public MaintenanceRequest updateMaintenanceRequest(String requestId, MaintenanceRequest requestData, String tenantId) {
        MaintenanceRequest request = repository.findByIdAndTenantId(requestId, tenantId)
                .orElseThrow(() -> new RuntimeException("Maintenance request not found or you are not authorized"));
        
        if (!request.getStatus().equals("Open") && !request.getStatus().equals("In Progress")) {
            throw new RuntimeException("Can only update Open or In Progress requests");
        }

        request.setType(requestData.getType());
        request.setDescription(requestData.getDescription());
        request.setUpdatedAt(LocalDateTime.now());
        return repository.save(request);
    }

    @Override
    public void deleteMaintenanceRequest(String requestId, String tenantId) {
        MaintenanceRequest request = repository.findByIdAndTenantId(requestId, tenantId)
                .orElseThrow(() -> new RuntimeException("Maintenance request not found or you are not authorized"));

        if (!request.getStatus().equals("Open") && !request.getStatus().equals("In Progress")) {
            throw new RuntimeException("Can only cancel Open or In Progress requests");
        }

        repository.delete(request);
    }

    @Override
    public MaintenanceRequest getMaintenanceRequestById(String requestId, String tenantId) {
        return repository.findByIdAndTenantId(requestId, tenantId).orElse(null);
    }

    @Override
    public MaintenanceRequest scheduleMaintenanceRequest(String requestId, LocalDateTime scheduledDate, String landlordId) {
        MaintenanceRequest request = repository.findByIdAndLandlordId(requestId, landlordId)
                .orElseThrow(() -> new RuntimeException("Maintenance request not found or you are not authorized"));

        request.setScheduledDate(scheduledDate);
        request.setStatus("In Progress");
        request.setUpdatedAt(LocalDateTime.now());
        return repository.save(request);
    }

    /**
     * Helper method to validate mandatory fields in MaintenanceRequest.
     * @param request MaintenanceRequest object to validate.
     */
    private void validateRequest(MaintenanceRequest request) {
        if (request.getTenantId() == null || request.getTenantId().isBlank()) {
            throw new IllegalArgumentException("Tenant ID is required");
        }
        if (request.getLeaseId() == null || request.getLeaseId().isBlank()) {
            throw new IllegalArgumentException("Lease ID is required");
        }
        if (request.getLandlordId() == null || request.getLandlordId().isBlank()) {
            throw new IllegalArgumentException("Landlord ID is required");
        }
        if (request.getType() == null || request.getType().isBlank()) {
            throw new IllegalArgumentException("Type is required");
        }
        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new IllegalArgumentException("Description is required");
        }
        if (request.getAddress() == null || request.getAddress().isBlank()) {
            throw new IllegalArgumentException("Address is required");
        }
    }
}
