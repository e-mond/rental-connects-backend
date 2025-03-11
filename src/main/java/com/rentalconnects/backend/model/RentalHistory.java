package com.rentalconnects.backend.model;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data // Lombok annotation to generate getters, setters, and toString automatically
@Document(collection = "rentalHistories") // Maps this class to the "rentalHistories" collection in MongoDB
public class RentalHistory {

    @Id
    private String id; // Unique identifier for the rental history record

    private String tenantId; // Reference to the tenant's User ID
    private String landlordId; // Reference to the landlord's User ID
    private String propertyId; // Reference to the Property ID
    private LocalDate startDate; // Start date of the rental period
    private LocalDate endDate; // End date of the rental period
    private double rentAmount; // Amount of rent paid for the period
    private String status; // e.g., "active", "completed", "terminated"
    private List<String> reviewIds; // References to Review IDs (if any)

    // Constructor (optional, for manual instantiation)
    public RentalHistory(String tenantId, String landlordId, String propertyId, LocalDate startDate, LocalDate endDate,
                         double rentAmount, String status, List<String> reviewIds) {
        this.tenantId = tenantId;
        this.landlordId = landlordId;
        this.propertyId = propertyId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.rentAmount = rentAmount;
        this.status = status;
        this.reviewIds = reviewIds;
    }
}
