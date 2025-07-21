package com.rentalconnects.backend.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rentalconnects.backend.dto.MaintenanceRequestDTO;
import com.rentalconnects.backend.dto.ScheduleRequestDTO;
import com.rentalconnects.backend.model.MaintenanceRequest;
import com.rentalconnects.backend.service.MaintenanceService;
import com.rentalconnects.backend.util.AuthUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/landlord/maintenance-requests") // Changed base path
public class LandlordMaintenanceController {

    private static final Logger log = LoggerFactory.getLogger(LandlordMaintenanceController.class);

    private final MaintenanceService maintenanceService;
    private final AuthUtils authUtils;

    public LandlordMaintenanceController(MaintenanceService maintenanceService, AuthUtils authUtils) {
        this.maintenanceService = maintenanceService;
        this.authUtils = authUtils;
    }

    @GetMapping
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<List<MaintenanceRequestDTO>> getMaintenanceRequests(
            @AuthenticationPrincipal UserDetails userDetails) {
        log.debug("processing get request for maintenance requests for user: {}", userDetails.getUsername());
        String landlordId = authUtils.getCurrentUserId();
        if (landlordId == null) {
            log.warn("no landlord id found for user: {}", userDetails.getUsername());
            return ResponseEntity.badRequest().build();
        }
        List<MaintenanceRequest> requests = maintenanceService.getMaintenanceRequestsByLandlordId(landlordId);
        List<MaintenanceRequestDTO> dtos = requests.stream().map(this::toDTO).collect(Collectors.toList());
        log.debug("returning {} maintenance requests for landlord: {}", dtos.size(), landlordId);
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<MaintenanceRequestDTO> scheduleMaintenanceRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ScheduleRequestDTO scheduleDTO) {
        log.debug("processing post request to schedule maintenance for user: {}", userDetails.getUsername());
        String landlordId = authUtils.getCurrentUserId();
        if (landlordId == null) {
            log.warn("no landlord id found for user: {}", userDetails.getUsername());
            return ResponseEntity.badRequest().build();
        }
        MaintenanceRequest updatedRequest = maintenanceService.scheduleMaintenanceRequest(
                scheduleDTO.getRequestId(), scheduleDTO.getScheduledDate(), landlordId);
        log.debug("scheduled maintenance request {} for landlord: {}", scheduleDTO.getRequestId(), landlordId);
        return ResponseEntity.ok(toDTO(updatedRequest));
    }

    private MaintenanceRequestDTO toDTO(MaintenanceRequest request) {
        MaintenanceRequestDTO dto = new MaintenanceRequestDTO();
        dto.setId(request.getId());
        dto.setTenantId(request.getTenantId());
        dto.setLeaseId(request.getLeaseId());
        dto.setLandlordId(request.getLandlordId());
        dto.setType(request.getType());
        dto.setDetails(request.getDescription());
        dto.setAddress(request.getAddress());
        dto.setStatus(request.getStatus());
        dto.setScheduledDate(request.getScheduledDate());
        dto.setCreatedAt(request.getCreatedAt());
        dto.setUpdatedAt(request.getUpdatedAt());
        return dto;
    }
}