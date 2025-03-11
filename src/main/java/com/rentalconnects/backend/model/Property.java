package com.rentalconnects.backend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data // Lombok annotation to generate getters, setters, and toString
@Document(collection = "properties") // Maps this class to the "properties" collection in MongoDB
@SuppressWarnings("unused") // Suppress "never read" warnings
public class Property {
    @Id
    private String id; // Unique identifier for the property
    private String landlordId; // ID of the landlord who owns the property
    private String address; // Property location
    private String type; // "short-term" or "long-term" rental type
    private double rent; // Rental price
}
