package com.rentalconnects.backend.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.rentalconnects.backend.dto.LeaseDTO;
import com.rentalconnects.backend.service.LeaseService;
import com.rentalconnects.backend.util.AuthUtils;

@RestController
@RequestMapping("/api/landlord/leases")
public class LeaseController {

    private final LeaseService leaseService;
    private final AuthUtils authUtils;

    public LeaseController(LeaseService leaseService, AuthUtils authUtils) {
        this.leaseService = leaseService;
        this.authUtils = authUtils;
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_LANDLORD')")
    public ResponseEntity<List<LeaseDTO>> getLeases() {
        String userId = authUtils.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        return ResponseEntity.ok(leaseService.getLeasesForLandlord(userId));
    }

    @GetMapping("/renewals")
    @PreAuthorize("hasAuthority('ROLE_LANDLORD')")
    public ResponseEntity<List<LeaseDTO>> getLeaseRenewals() {
        String userId = authUtils.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        return ResponseEntity.ok(leaseService.getUpcomingLeaseRenewals(userId));
    }

    public LeaseService getLeaseService() {
        return leaseService;
    }

    public AuthUtils getAuthUtils() {
        return authUtils;
    }
}