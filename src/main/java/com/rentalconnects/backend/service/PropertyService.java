package com.rentalconnects.backend.service;

import com.rentalconnects.backend.model.Property;
import com.rentalconnects.backend.model.Viewing;
import com.rentalconnects.backend.model.RentalApplication;
import com.rentalconnects.backend.model.MaintenanceRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface PropertyService {

    Property createProperty(Property property);

    Property createPropertyWithLandlordId(Property property, List<MultipartFile> images, String landlordId);

    Property createPropertyWithLandlordId(Property property, List<MultipartFile> images, String landlordId, Integer primaryImageIndex);

    Property createProperty(Property property, List<MultipartFile> images, Integer primaryImageIndex) throws IOException;

    Optional<Property> getPropertyById(String id);

    List<Property> getAllActiveProperties();

    List<Property> getAllProperties();

    Property findById(String propertyId);

    List<Property> findAllAvailable();

    List<Property> getPropertiesByLandlordId(String landlordId);

    Property updatePropertyWithLandlordId(Property property, List<MultipartFile> images, String landlordId);

    Property updateProperty(Property property, List<MultipartFile> images, Integer primaryImageIndex) throws IOException;

    Property updateProperty(Property property, List<MultipartFile> images, String landlordId, Integer primaryImageIndex, List<String> removedImages) throws IOException;

    void deleteProperty(String id);

    void deleteLandlordProperty(String id, String landlordId);

    List<Property> getPropertiesByMaxRent(double rent);

    List<Property> searchPropertiesByAddress(String address);

    List<Property> getPropertiesByType(String type);

    List<Property> getFilteredProperties(String location, Double priceMin, Double priceMax, String propertyType, Integer bedrooms);

    void scheduleViewing(Viewing viewing);

    void applyForProperty(RentalApplication application);

    void submitMaintenanceRequest(MaintenanceRequest request);

    void fixImageUrls();

    String getPropertyNameById(String propertyId); // Added method
}