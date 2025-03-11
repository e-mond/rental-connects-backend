package com.rentalconnects.backend.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.rentalconnects.backend.model.Property;

public interface PropertyRepository extends MongoRepository<Property, String> {
    // Find properties by landlord ID
    List<Property> findByLandlordId(String landlordId);

    // Find properties by address (case-insensitive)
    List<Property> findByAddressContainingIgnoreCase(String address);

    // Find properties by type (e.g., "short-term" or "long-term")
    List<Property> findByType(String type);

    // Find properties with rent less than or equal to a value
    List<Property> findByRentLessThanEqual(double rent);
}