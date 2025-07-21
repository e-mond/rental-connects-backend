package com.rentalconnects.backend.model;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // No-args constructor for serialization
@Document(collection = "rentalHistories")
public class RentalHistory {
    @Id
    private String id;
    private String tenantId;
    private String landlordId;
    private String propertyId;
    private LocalDate startDate;
    private LocalDate endDate;
    private double rentAmount;
    private String status;
    private List<String> reviewIds;
}
