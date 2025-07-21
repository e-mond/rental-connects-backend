package com.rentalconnects.backend.controller;

import java.util.List;
import java.util.stream.Collectors;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rentalconnects.backend.dto.MaintenanceRequestDTO;
import com.rentalconnects.backend.model.MaintenanceRequest;
import com.rentalconnects.backend.service.MaintenanceService;

import jakarta.validation.Valid;

/**
 * Controller for handling tenant maintenance request-related API endpoints.
 * Provides endpoints for creating, retrieving, updating, and canceling maintenance requests.
 */
@RestController
@RequestMapping("/api/tenant/maintenance")
public class TenantMaintenanceController {

    private final MaintenanceService maintenanceService;

    
    public TenantMaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    /**
     * Creates a new maintenance request for the authenticated tenant.
     * @param dto The maintenance request data
     * @return The created maintenance request DTO
     */
    @PostMapping
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<MaintenanceRequestDTO> createMaintenanceRequest(@Valid @RequestBody MaintenanceRequestDTO dto) {
        String tenantId = SecurityContextHolder.getContext().getAuthentication().getName();
        MaintenanceRequest request = toEntity(dto);
        request.setTenantId(tenantId);
        MaintenanceRequest createdRequest = maintenanceService.createMaintenanceRequest(request);
        return new ResponseEntity<>(toDTO(createdRequest), HttpStatus.CREATED);
    }

    /**
     * Retrieves all maintenance requests for the authenticated tenant.
     * @return List of maintenance request DTOs
     */
    @GetMapping
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<List<MaintenanceRequestDTO>> getMaintenanceRequests() {
        String tenantId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<MaintenanceRequest> requests = maintenanceService.getMaintenanceRequestsByTenantId(tenantId);
        List<MaintenanceRequestDTO> dtos = requests.stream().map(this::toDTO).collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    /**
     * Retrieves a specific maintenance request by ID for the authenticated tenant.
     * @param id The ID of the maintenance request
     * @return The maintenance request DTO
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<MaintenanceRequestDTO> getMaintenanceRequestById(@PathVariable String id) {
        String tenantId = SecurityContextHolder.getContext().getAuthentication().getName();
        MaintenanceRequest request = maintenanceService.getMaintenanceRequestById(id, tenantId);
        if (request == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(toDTO(request));
    }

    /**
     * Updates a maintenance request for the authenticated tenant.
     * @param id The ID of the maintenance request
     * @param dto The updated maintenance request data
     * @return The updated maintenance request DTO
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<MaintenanceRequestDTO> updateMaintenanceRequest(
            @PathVariable String id, @Valid @RequestBody MaintenanceRequestDTO dto) {
        String tenantId = SecurityContextHolder.getContext().getAuthentication().getName();
        MaintenanceRequest request = toEntity(dto);
        MaintenanceRequest updatedRequest = maintenanceService.updateMaintenanceRequest(id, request, tenantId);
        return ResponseEntity.ok(toDTO(updatedRequest));
    }

    /**
     * Cancels a maintenance request for the authenticated tenant.
     * @param id The ID of the maintenance request
     * @return No content on success
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<Void> cancelMaintenanceRequest(@PathVariable String id) {
        String tenantId = SecurityContextHolder.getContext().getAuthentication().getName();
        maintenanceService.deleteMaintenanceRequest(id, tenantId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Converts a MaintenanceRequest entity to a DTO.
     * @param request The maintenance request entity
     * @return The maintenance request DTO
     */
    private MaintenanceRequestDTO toDTO(MaintenanceRequest request) {
        MaintenanceRequestDTO dto = new MaintenanceRequestDTO();
        dto.setId(request.getId());
        dto.setTenantId(request.getTenantId());
        dto.setLeaseId(request.getLeaseId());
        dto.setLandlordId(request.getLandlordId());
        dto.setType(request.getType());
        dto.setDetails(request.getDescription()); // Map description to details
        dto.setAddress(request.getAddress());
        dto.setStatus(request.getStatus());
        dto.setScheduledDate(request.getScheduledDate());
        dto.setCreatedAt(request.getCreatedAt());
        dto.setUpdatedAt(request.getUpdatedAt());
        return dto;
    }

    /**
     * Converts a MaintenanceRequestDTO to an entity.
     * @param dto The maintenance request DTO
     * @return The maintenance request entity
     */
    private MaintenanceRequest toEntity(MaintenanceRequestDTO dto) {
        MaintenanceRequest request = new MaintenanceRequest();
        request.setId(dto.getId());
        request.setTenantId(dto.getTenantId());
        request.setLeaseId(dto.getLeaseId());
        request.setLandlordId(dto.getLandlordId());
        request.setType(dto.getType());
        request.setDescription(dto.getDetails()); // Map details to description
        request.setAddress(dto.getAddress());
        request.setStatus(dto.getStatus());
        request.setScheduledDate(dto.getScheduledDate());
        request.setCreatedAt(dto.getCreatedAt());
        request.setUpdatedAt(dto.getUpdatedAt());
        return request;
    }
}