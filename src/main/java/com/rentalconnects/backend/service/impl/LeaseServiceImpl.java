package com.rentalconnects.backend.service.impl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.rentalconnects.backend.dto.LeaseDTO;
import com.rentalconnects.backend.model.Lease;
import com.rentalconnects.backend.model.User;
import com.rentalconnects.backend.repository.LeaseRepository;
import com.rentalconnects.backend.repository.PropertyRepository;
import com.rentalconnects.backend.service.LeaseService;
import com.rentalconnects.backend.service.UserService;

@Service
public class LeaseServiceImpl implements LeaseService {

    private final LeaseRepository leaseRepository;
    private final PropertyRepository propertyRepository;
    private final UserService userService;

    public LeaseServiceImpl(LeaseRepository leaseRepository, PropertyRepository propertyRepository, UserService userService) {
        this.leaseRepository = leaseRepository;
        this.propertyRepository = propertyRepository;
        this.userService = userService;
    }

    @Override
    public List<LeaseDTO> getLeasesForLandlord(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("Landlord ID must not be null or empty");
        }
        List<Lease> leases = leaseRepository.findByLandlordId(userId);
        return leases.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<Lease> getLeasesByLandlordId(String landlordId) {
        if (landlordId == null || landlordId.isEmpty()) {
            throw new IllegalArgumentException("Landlord ID must not be null or empty");
        }
        return leaseRepository.findByLandlordId(landlordId);
    }

    @Override
    public List<LeaseDTO> getLeasesByTenantId(String tenantId) {
        if (tenantId == null || tenantId.isEmpty()) {
            throw new IllegalArgumentException("Tenant ID must not be null or empty");
        }
        List<Lease> leases = leaseRepository.findByTenantId(tenantId);
        return leases.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<LeaseDTO> getUpcomingLeaseRenewals(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("Landlord ID must not be null or empty");
        }
        List<Lease> leases = leaseRepository.findByLandlordId(userId);
        return leases.stream()
                .filter(lease -> {
                    if (lease.getEndDate() == null) return false;
                    long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), lease.getEndDate());
                    return daysRemaining >= 0 && daysRemaining <= 30;
                })
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private LeaseDTO convertToDTO(Lease lease) {
        LeaseDTO dto = new LeaseDTO();
        dto.setId(lease.getId());
        dto.setTenantId(lease.getTenantId());
        dto.setLandlordId(lease.getLandlordId());
        dto.setPropertyId(lease.getPropertyId());

        // Fetch property details
        propertyRepository.findById(lease.getPropertyId())
                .ifPresent(property -> {
                    dto.setProperty(property.getTitle());
                    dto.setPropertyAddress(property.getAddress() != null ? property.getAddress() : property.getTitle());
                });

        // Fetch tenant name
        String tenantName = lease.getTenantId();
        try {
            User tenant = userService.getUserById(lease.getTenantId())
                                     .orElse(null); // Unwrap Optional, fallback to null
            tenantName = tenant != null && tenant.getFullName() != null ? tenant.getFullName() : tenantName;
        } catch (IllegalArgumentException e) {
            System.err.println("Tenant not found for ID: " + lease.getTenantId());
        }
        dto.setTenant(tenantName);

        // Calculate days remaining
        long daysRemaining = 0L;
        if (lease.getEndDate() != null) {
            LocalDate today = LocalDate.now();
            daysRemaining = ChronoUnit.DAYS.between(today, lease.getEndDate());
            daysRemaining = Math.max(daysRemaining, 0);
        }
        dto.setDaysRemaining(daysRemaining);

        // Set renewal date
        if (lease.getEndDate() != null) {
            dto.setRenewalDate(lease.getEndDate().toString());
        }

        // Set rent
        dto.setRent(lease.getMonthlyRent());

        // Set status
        dto.setStatus(lease.getStatus());

        return dto;
    }
}