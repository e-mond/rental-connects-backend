package com.rentalconnects.backend.service;

import java.util.List;
import java.util.Map;

import com.rentalconnects.backend.model.MaintenanceRequest;
import com.rentalconnects.backend.model.Property;

public interface LandlordService {
    Map<String, Object> getDashboardData(String landlordId);
    List<MaintenanceRequest> getMaintenanceRequests(String landlordId);
    List<Property> getPropertiesByLandlordId(String landlordId);
    Property getPropertyById(String propertyId);
    Property createProperty(Property property);
    Property updateProperty(Property property);
    void deleteProperty(String propertyId);
}