package com.rentalconnects.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.rentalconnects.backend.model.Property;
import com.rentalconnects.backend.model.RentalRequest;
import com.rentalconnects.backend.model.User;
import com.rentalconnects.backend.repository.PropertyRepository;
import com.rentalconnects.backend.repository.RentalRequestRepository;
import com.rentalconnects.backend.repository.UserRepository;

@Service
public class RentalRequestService {

    private final RentalRequestRepository rentalRequestRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;

    public RentalRequestService(RentalRequestRepository rentalRequestRepository, UserRepository userRepository, PropertyRepository propertyRepository) {
        this.rentalRequestRepository = rentalRequestRepository;
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
    }

    /**
     * Create a rental request using tenant ID and property ID.
     */
    public RentalRequest createRequest(String tenantId, String propertyId) {
        RentalRequest request = new RentalRequest();
        request.setTenantId(tenantId);
        request.setPropertyId(propertyId);
        request.setStatus("PENDING");
        request.setCreatedAt(LocalDateTime.now()); // Changed from setSubmittedDate
        return rentalRequestRepository.save(request);
    }

    /**
     * Create a rental request using the tenant's email.
     */
    public RentalRequest createRequestFromEmail(String tenantEmail, String propertyId) {
        User tenant = userRepository.findByEmail(tenantEmail)
                .orElseThrow(() -> new RuntimeException("Tenant not found with email: " + tenantEmail));

        RentalRequest request = new RentalRequest();
        request.setTenantId(tenant.getId());
        request.setPropertyId(propertyId);
        request.setStatus("PENDING");
        request.setCreatedAt(LocalDateTime.now()); // Changed from setSubmittedDate
        return rentalRequestRepository.save(request);
    }

    /**
     * Get all requests for a specific tenant, with propertyTitle.
     */
    public List<RentalRequestDTO> getTenantRequests(String tenantId) {
        List<RentalRequest> requests = rentalRequestRepository.findByTenantId(tenantId);
        return requests.stream()
                .map(request -> {
                    Property property = propertyRepository.findById(request.getPropertyId())
                            .orElseThrow(() -> new RuntimeException("Property not found: " + request.getPropertyId()));
                    return new RentalRequestDTO(
                            request.getId(),
                            property.getTitle(),
                            request.getStatus(),
                            request.getCreatedAt() // Changed from getSubmittedDate
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * Get all rental requests for a landlord's property.
     */
    public List<RentalRequest> getPropertyRequests(String propertyId) {
        return rentalRequestRepository.findByPropertyId(propertyId);
    }

    /**
     * Update the status of a rental request (Approve or Reject).
     */
    public RentalRequest updateStatus(String requestId, String status) {
        Optional<RentalRequest> requestOpt = rentalRequestRepository.findById(requestId);
        if (requestOpt.isPresent()) {
            RentalRequest request = requestOpt.get();
            request.setStatus(status);
            return rentalRequestRepository.save(request);
        }
        throw new RuntimeException("Rental request not found: " + requestId);
    }

    /**
     * DTO for frontend compatibility.
     */
    public static class RentalRequestDTO {
        private final String id;
        private final String propertyTitle;
        private final String status;
        private final LocalDateTime submittedDate; // Kept as submittedDate for DTO consistency

        public RentalRequestDTO(String id, String propertyTitle, String status, LocalDateTime submittedDate) {
            this.id = id;
            this.propertyTitle = propertyTitle;
            this.status = status;
            this.submittedDate = submittedDate;
        }

        public String getId() {
            return id;
        }

        public String getPropertyTitle() {
            return propertyTitle;
        }

        public String getStatus() {
            return status;
        }

        public LocalDateTime getSubmittedDate() {
            return submittedDate;
        }
    }
}