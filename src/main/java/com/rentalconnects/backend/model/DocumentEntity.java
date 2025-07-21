package com.rentalconnects.backend.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Represents a document entity uploaded by a landlord in the RentalConnects application.
 * This model is mapped to the "documents" collection in MongoDB.
 */
@Document(collection = "documents")
public class DocumentEntity {

    @Id
    private String id;

    @NotBlank(message = "Document name is required")
    private String name;

    @NotBlank(message = "Landlord ID is required")
    private String landlordId;

    @NotBlank(message = "File URL is required")
    private String url;

    private String category;

    @NotNull(message = "Updated date cannot be null")
    private LocalDateTime updated;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLandlordId() {
        return landlordId;
    }

    public void setLandlordId(String landlordId) {
        this.landlordId = landlordId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }
}