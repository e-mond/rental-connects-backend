package com.rentalconnects.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rentalconnects.backend.model.Property;
import com.rentalconnects.backend.service.PropertyService;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    @PostMapping
    public Property createProperty(@RequestBody Property property) {
        return propertyService.createProperty(property);
    }

    @GetMapping("/{id}")
    public Property getProperty(@PathVariable String id) {
        return propertyService.getPropertyById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
    }

    @GetMapping("/landlord/{landlordId}")
    public List<Property> getPropertiesByLandlordId(@PathVariable String landlordId) {
        return propertyService.getPropertiesByLandlordId(landlordId);
    }

    @GetMapping("/search/address")
    public List<Property> searchPropertiesByAddress(@RequestParam String address) {
        return propertyService.searchPropertiesByAddress(address);
    }

    @GetMapping("/type/{type}")
    public List<Property> getPropertiesByType(@PathVariable String type) {
        return propertyService.getPropertiesByType(type);
    }

    @GetMapping("/rent/max")
    public List<Property> getPropertiesByMaxRent(@RequestParam double rent) {
        return propertyService.getPropertiesByMaxRent(rent);
    }

    @DeleteMapping("/{id}")
    public void deleteProperty(@PathVariable String id) {
        propertyService.deleteProperty(id);
    }
}