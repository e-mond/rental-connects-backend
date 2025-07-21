package com.rentalconnects.backend.service.impl;

import com.rentalconnects.backend.model.RentalApplication;
import com.rentalconnects.backend.repository.RentalApplicationRepository;
import com.rentalconnects.backend.service.RentalApplicationService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of the RentalApplicationService interface for handling rental application-related operations.
 */
@Service
public class RentalApplicationServiceImpl implements RentalApplicationService {

    private final RentalApplicationRepository rentalApplicationRepository;

    public RentalApplicationServiceImpl(RentalApplicationRepository rentalApplicationRepository) {
        this.rentalApplicationRepository = rentalApplicationRepository;
    }

    @Override
    public List<RentalApplication> getApplicationsByTenantId(String tenantId) {
        if (tenantId == null || tenantId.isEmpty()) {
            throw new IllegalArgumentException("Tenant ID must not be null or empty");
        }
        return rentalApplicationRepository.findByTenantId(tenantId);
    }
}