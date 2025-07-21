package com.rentalconnects.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivityRequest {
    private String landlordId;
    @NotNull(message = "Type cannot be null")
    private String type;
    @NotNull(message = "Message cannot be null")
    private String message;
    @NotNull(message = "Entity ID cannot be null")
    private String entityId;
    @NotNull(message = "Property ID cannot be null")
    private String propertyId;

    public ActivityRequest() {}

    public ActivityRequest(String landlordId, String type, String message, String entityId, String propertyId) {
        this.landlordId = landlordId;
        this.type = type;
        this.message = message;
        this.entityId = entityId;
        this.propertyId = propertyId;
    }
}