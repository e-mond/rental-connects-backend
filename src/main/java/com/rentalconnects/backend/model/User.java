package com.rentalconnects.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data // Lombok annotation to generate getters, setters, and toString automatically
@Document(collection = "users") // Maps this class to the "users" collection in MongoDB
public class User {
    @Id
    private String id; // Unique identifier for the user
    private String username; // User's display name
    private String email; // User's email address (used for authentication)
    private String password; // This field will hold the hashed password
    private String role; // Defines user type: "LANDLORD" or "TENANT"
}
