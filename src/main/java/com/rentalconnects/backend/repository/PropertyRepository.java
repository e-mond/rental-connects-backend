package com.rentalconnects.backend.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.rentalconnects.backend.model.Property;

/**
 * Repository interface for Property entity, providing CRUD operations and custom queries.
 */
public interface PropertyRepository extends MongoRepository<Property, String> {
    List<Property> findByLandlordId(String landlordId); // Find properties by landlord ID
    List<Property> findByAddressContainingIgnoreCase(String address); // Find properties by address (case-insensitive)
    List<Property> findByPropertyType(String propertyType); // Find properties by property type
    List<Property> findByRentLessThanEqual(double rent); 
    List<Property> findByStatus(String status); // Find properties by status
    List<Property> findByStatusIn(List<String> statuses);
}