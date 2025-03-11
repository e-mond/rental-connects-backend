package com.rentalconnects.backend.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.rentalconnects.backend.model.RentalHistory; 

public interface RentalHistoryRepository extends MongoRepository<RentalHistory, String> {
    // Find all rental histories for a tenant
    List<RentalHistory> findByTenantId(String tenantId);
    
    // Find all rental histories for a landlord
    List<RentalHistory> findByLandlordId(String landlordId); 
}