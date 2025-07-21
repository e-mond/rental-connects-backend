package com.rentalconnects.backend.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "properties")
public class Property {
    
    @Id
    private String id;
    
    private String landlordId;
    private String title;
    private String description;
    private Integer bedrooms;
    private Integer bathrooms;
    private String address;
    private String location;
    private Double rent;
    private String currency;
    private Integer squareFeet;
    private Integer builtYear;
    private LocalDate availableFrom;
    private Boolean utilitiesIncluded;
    private List<String> amenities;
    private String status;
    private String propertyType;
    private List<String> imageUrls;
    private String primaryImageUrl;
    private Boolean isSharedBedrooms;
    private Boolean isSharedBathrooms;
    private Double rating;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public Property() {}

    // Getters
    public String getId() { return id; }
    public String getLandlordId() { return landlordId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Integer getBedrooms() { return bedrooms; }
    public Integer getBathrooms() { return bathrooms; }
    public String getAddress() { return address; }
    public String getLocation() { return location; }
    public Double getRent() { return rent; }
    public String getCurrency() { return currency; }
    public Integer getSquareFeet() { return squareFeet; }
    public Integer getBuiltYear() { return builtYear; }
    public LocalDate getAvailableFrom() { return availableFrom; }
    public Boolean getUtilitiesIncluded() { return utilitiesIncluded; }
    public List<String> getAmenities() { return amenities; }
    public String getStatus() { return status; }
    public String getPropertyType() { return propertyType; }
    public List<String> getImageUrls() { return imageUrls; }
    public String getPrimaryImageUrl() { return primaryImageUrl; }
    public Boolean getIsSharedBedrooms() { return isSharedBedrooms; }
    public Boolean getIsSharedBathrooms() { return isSharedBathrooms; }
    public Double getRating() { return rating; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setLandlordId(String landlordId) { this.landlordId = landlordId; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setBedrooms(Integer bedrooms) { this.bedrooms = bedrooms; }
    public void setBathrooms(Integer bathrooms) { this.bathrooms = bathrooms; }
    public void setAddress(String address) { this.address = address; }
    public void setLocation(String location) { this.location = location; }
    public void setRent(Double rent) { this.rent = rent; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setSquareFeet(Integer squareFeet) { this.squareFeet = squareFeet; }
    public void setBuiltYear(Integer builtYear) { this.builtYear = builtYear; }
    public void setAvailableFrom(LocalDate availableFrom) { this.availableFrom = availableFrom; }
    public void setUtilitiesIncluded(Boolean utilitiesIncluded) { this.utilitiesIncluded = utilitiesIncluded; }
    public void setAmenities(List<String> amenities) { this.amenities = amenities; }
    public void setStatus(String status) { this.status = status; }
    public void setPropertyType(String propertyType) { this.propertyType = propertyType; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
    public void setPrimaryImageUrl(String primaryImageUrl) { this.primaryImageUrl = primaryImageUrl; }
    public void setIsSharedBedrooms(Boolean isSharedBedrooms) { this.isSharedBedrooms = isSharedBedrooms; }
    public void setIsSharedBathrooms(Boolean isSharedBathrooms) { this.isSharedBathrooms = isSharedBathrooms; }
    public void setRating(Double rating) { this.rating = rating; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}