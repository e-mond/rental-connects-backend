package com.rentalconnects.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data // Lombok annotation to generate getters, setters, and toString automatically
@Document(collection = "reviews") // Maps this class to the "reviews" collection in MongoDB
public class Review {
    @Id
    private String id; // Unique identifier for the review
    private String authorId; // Tenant or Landlord ID who wrote the review
    private String targetId; // Tenant or Landlord ID being reviewed
    private int rating; // Rating score, e.g., 1-5
    private String comment; // Review comment or feedback
}