package com.rentalconnects.backend.controller;

import jakarta.validation.constraints.NotBlank;

public class ActivityRequest {
    private String landlordId;
    @NotBlank
    private String type;
    @NotBlank
    private String message;
    @NotBlank
    private String entityId;
    @NotBlank
    private String propertyId;

    public ActivityRequest() {}

    public ActivityRequest(String landlordId, String type, String message, String entityId, String propertyId){
        this.landlordId = landlordId;
        this.type = type;
        this.message = message;
        this.entityId = entityId;
        this.propertyId = propertyId;
    }

    public String getLandlordId() { return landlordId; }
    public void setLandlordId(String landlordId) { this.landlordId = landlordId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
    public String getpropertyId() { return propertyId; }
    public void propertyId(String propertyId) { this.propertyId = propertyId; }
}