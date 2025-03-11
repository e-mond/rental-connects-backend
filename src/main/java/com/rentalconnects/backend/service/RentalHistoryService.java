package com.rentalconnects.backend.service;

import com.rentalconnects.backend.model.RentalHistory;
import com.rentalconnects.backend.repository.RentalHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RentalHistoryService {

    @Autowired
    private RentalHistoryRepository rentalHistoryRepository;

    // Save a new rental history record
    public RentalHistory createRentalHistory(RentalHistory rentalHistory) {
        return rentalHistoryRepository.save(rentalHistory);
    }

    // Retrieve a rental history record by its ID
    public Optional<RentalHistory> getRentalHistoryById(String id) {
        return rentalHistoryRepository.findById(id);
    }

    // Get all rental histories for a specific tenant
    public List<RentalHistory> getRentalHistoriesByTenantId(String tenantId) {
        return rentalHistoryRepository.findByTenantId(tenantId);
    }

    // Get all rental histories for a specific landlord
    public List<RentalHistory> getRentalHistoriesByLandlordId(String landlordId) {
        return rentalHistoryRepository.findByLandlordId(landlordId);
    }

    // Delete a rental history record by its ID
    public void deleteRentalHistory(String id) {
        rentalHistoryRepository.deleteById(id);
    }
}