package com.rentalconnects.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rentalconnects.backend.model.RentalHistory;
import com.rentalconnects.backend.service.RentalHistoryService;

@RestController
@RequestMapping("/api/rental-histories")
public class RentalHistoryController {

    @Autowired
    private RentalHistoryService rentalHistoryService;

    @PostMapping
    public RentalHistory createRentalHistory(@RequestBody RentalHistory rentalHistory) {
        return rentalHistoryService.createRentalHistory(rentalHistory);
    }

    @GetMapping("/{id}")
    public RentalHistory getRentalHistory(@PathVariable String id) {
        return rentalHistoryService.getRentalHistoryById(id)
                .orElseThrow(() -> new RuntimeException("Rental history not found"));
    }

    @GetMapping("/tenant/{tenantId}")
    public List<RentalHistory> getRentalHistoriesByTenantId(@PathVariable String tenantId) {
        return rentalHistoryService.getRentalHistoriesByTenantId(tenantId);
    }

    @GetMapping("/landlord/{landlordId}")
    public List<RentalHistory> getRentalHistoriesByLandlordId(@PathVariable String landlordId) {
        return rentalHistoryService.getRentalHistoriesByLandlordId(landlordId);
    }

    @DeleteMapping("/{id}")
    public void deleteRentalHistory(@PathVariable String id) {
        rentalHistoryService.deleteRentalHistory(id);
    }
}