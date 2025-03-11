package com.rentalconnects.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rentalconnects.backend.model.Property;
import com.rentalconnects.backend.repository.PropertyRepository;

@Service
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    // Create and save a new property
    public Property createProperty(Property property) {
        return propertyRepository.save(property);
    }

    // Retrieve a property by its ID
    public Optional<Property> getPropertyById(String id) {
        return propertyRepository.findById(id);
    }

    // Get all properties owned by a specific landlord
    public List<Property> getPropertiesByLandlordId(String landlordId) {
        return propertyRepository.findByLandlordId(landlordId);
    }

    // Search for properties based on address (case-insensitive)
    public List<Property> searchPropertiesByAddress(String address) {
        return propertyRepository.findByAddressContainingIgnoreCase(address);
    }

    // Retrieve properties based on their type (short-term or long-term)
    public List<Property> getPropertiesByType(String type) {
        return propertyRepository.findByType(type);
    }

    // Get properties that have rent equal to or below the specified amount
    public List<Property> getPropertiesByMaxRent(double rent) {
        return propertyRepository.findByRentLessThanEqual(rent);
    }

    // Delete a property by its ID
    public void deleteProperty(String id) {
        propertyRepository.deleteById(id);
    }
}