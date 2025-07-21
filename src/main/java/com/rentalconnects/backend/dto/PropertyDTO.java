package com.rentalconnects.backend.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

/**
 * DTO for creating/updating a Property.
 * Excludes landlordId since it's set server-side.
 */
public class PropertyDTO {

    private String id;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Number of bedrooms is required")
    @Min(value = 0, message = "Bedrooms cannot be negative")
    private Integer bedrooms;

    @NotNull(message = "Number of bathrooms is required")
    @Min(value = 0, message = "Bathrooms cannot be negative")
    private Integer bathrooms;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private Double price;

    @NotBlank(message = "Currency is required")
    @Pattern(regexp = "GHS|USD|EUR", message = "Currency must be GHS, USD, or EUR")
    private String currency;

    @Positive(message = "Square feet must be positive")
    private Integer squareFeet;

    @Min(value = 1900, message = "Built year must be after 1900")
    private Integer builtYear;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate availableFrom;

    private Boolean utilitiesIncluded;

    private List<String> amenities;

    @NotBlank(message = "Status is required")
    private String status;

    @NotBlank(message = "Property type is required")
    private String propertyType;

    private List<String> imageUrls;

    private String primaryImageUrl;

    private Boolean isSharedBedrooms;

    private Boolean isSharedBathrooms;

    private Double rating;

    private Boolean parkingIncluded;

    private Boolean centralAC;

    private Boolean inUnitLaundry;

    private Integer photos; // Added to match frontend data

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getBedrooms() { return bedrooms; }
    public void setBedrooms(Integer bedrooms) { this.bedrooms = bedrooms; }
    public Integer getBathrooms() { return bathrooms; }
    public void setBathrooms(Integer bathrooms) { this.bathrooms = bathrooms; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public Integer getSquareFeet() { return squareFeet; }
    public void setSquareFeet(Integer squareFeet) { this.squareFeet = squareFeet; }
    public Integer getBuiltYear() { return builtYear; }
    public void setBuiltYear(Integer builtYear) { this.builtYear = builtYear; }
    public LocalDate getAvailableFrom() { return availableFrom; }
    public void setAvailableFrom(LocalDate availableFrom) { this.availableFrom = availableFrom; }
    public Boolean getUtilitiesIncluded() { return utilitiesIncluded; }
    public void setUtilitiesIncluded(Boolean utilitiesIncluded) { this.utilitiesIncluded = utilitiesIncluded; }
    public List<String> getAmenities() { return amenities; }
    public void setAmenities(List<String> amenities) { this.amenities = amenities; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPropertyType() { return propertyType; }
    public void setPropertyType(String propertyType) { this.propertyType = propertyType; }
    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
    public String getPrimaryImageUrl() { return primaryImageUrl; }
    public void setPrimaryImageUrl(String primaryImageUrl) { this.primaryImageUrl = primaryImageUrl; }
    public Boolean getIsSharedBedrooms() { return isSharedBedrooms; }
    public void setIsSharedBedrooms(Boolean isSharedBedrooms) { this.isSharedBedrooms = isSharedBedrooms; }
    public Boolean getIsSharedBathrooms() { return isSharedBathrooms; }
    public void setIsSharedBathrooms(Boolean isSharedBathrooms) { this.isSharedBathrooms = isSharedBathrooms; }
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    public Boolean getParkingIncluded() { return parkingIncluded; }
    public void setParkingIncluded(Boolean parkingIncluded) { this.parkingIncluded = parkingIncluded; }
    public Boolean getCentralAC() { return centralAC; }
    public void setCentralAC(Boolean centralAC) { this.centralAC = centralAC; }
    public Boolean getInUnitLaundry() { return inUnitLaundry; }
    public void setInUnitLaundry(Boolean inUnitLaundry) { this.inUnitLaundry = inUnitLaundry; }
    public Integer getPhotos() { return photos; } // Added getter
    public void setPhotos(Integer photos) { this.photos = photos; } // Added setter
}