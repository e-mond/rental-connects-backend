package com.rentalconnects.backend.controller;

import com.rentalconnects.backend.dto.PropertyDTO;
import com.rentalconnects.backend.model.Property;
import com.rentalconnects.backend.service.PropertyService;
import com.rentalconnects.backend.util.AuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PropertyController {

    private static final Logger logger = LoggerFactory.getLogger(PropertyController.class);

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private AuthUtils authUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/properties")
    public ResponseEntity<List<Property>> getAllActiveProperties() {
        logger.info("[PropertyController] Fetching all active properties");
        List<Property> properties = propertyService.getAllActiveProperties();
        logger.info("[PropertyController] Returning {} properties", properties.size());
        return ResponseEntity.ok(properties);
    }

    @PostMapping("/properties")
    public ResponseEntity<Property> createProperty(@RequestBody @Valid Property property) {
        logger.info("[PropertyController] Creating property: {}", property.getTitle());
        Property savedProperty = propertyService.createProperty(property);
        logger.info("[PropertyController] Property created with ID: {}", savedProperty.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProperty);
    }

    @GetMapping("/properties/{id}")
    public ResponseEntity<Property> getProperty(@PathVariable String id) {
        logger.info("[PropertyController] Fetching property with ID: {}", id);
        if (!isValidObjectId(id)) {
            logger.warn("[PropertyController] Invalid ID format: {}", id);
            return ResponseEntity.badRequest().body(null);
        }
        return propertyService.getPropertyById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    logger.warn("[PropertyController] Property not found with ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/properties/landlord/{landlordId}")
    public ResponseEntity<List<Property>> getPropertiesByLandlordId(@PathVariable String landlordId) {
        logger.info("[PropertyController] Fetching properties for landlord ID: {}", landlordId);
        if (!isValidObjectId(landlordId)) {
            logger.warn("[PropertyController] Invalid landlord ID format: {}", landlordId);
            return ResponseEntity.badRequest().body(null);
        }
        List<Property> properties = propertyService.getPropertiesByLandlordId(landlordId);
        logger.info("[PropertyController] Returning {} properties", properties.size());
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/properties/search/address")
    public ResponseEntity<List<Property>> searchPropertiesByAddress(@RequestParam String address) {
        logger.info("[PropertyController] Searching properties by address: {}", address);
        if (address == null || address.trim().isEmpty()) {
            logger.warn("[PropertyController] Invalid address provided");
            return ResponseEntity.badRequest().body(null);
        }
        List<Property> properties = propertyService.searchPropertiesByAddress(address);
        logger.info("[PropertyController] Returning {} properties", properties.size());
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/properties/type/{type}")
    public ResponseEntity<List<Property>> getPropertiesByType(@PathVariable String type) {
        logger.info("[PropertyController] Fetching properties by type: {}", type);
        if (type == null || type.trim().isEmpty()) {
            logger.warn("[PropertyController] Invalid type provided");
            return ResponseEntity.badRequest().body(null);
        }
        List<Property> properties = propertyService.getPropertiesByType(type);
        logger.info("[PropertyController] Returning {} properties", properties.size());
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/properties/rent/max")
    public ResponseEntity<List<Property>> getPropertiesByMaxRent(@RequestParam double rent) {
        logger.info("[PropertyController] Fetching properties with max rent: {}", rent);
        if (rent <= 0) {
            logger.warn("[PropertyController] Invalid rent value: {}", rent);
            return ResponseEntity.badRequest().body(null);
        }
        List<Property> properties = propertyService.getPropertiesByMaxRent(rent);
        logger.info("[PropertyController] Returning {} properties", properties.size());
        return ResponseEntity.ok(properties);
    }

    @DeleteMapping("/properties/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable String id) {
        logger.info("[PropertyController] Deleting property with ID: {}", id);
        if (!isValidObjectId(id)) {
            logger.warn("[PropertyController] Invalid ID format: {}", id);
            return ResponseEntity.badRequest().build();
        }
        propertyService.deleteProperty(id);
        logger.info("[PropertyController] Property deleted with ID: {}", id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/landlord/properties")
    @PreAuthorize("hasAuthority('ROLE_LANDLORD')")
    public ResponseEntity<List<Property>> getLandlordProperties() {
        logger.info("[PropertyController] Fetching properties for current landlord");
        try {
            String landlordId = authUtils.getCurrentUserId();
            if (landlordId == null) {
                logger.warn("[PropertyController] Unauthorized: No landlordId found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            List<Property> properties = propertyService.getPropertiesByLandlordId(landlordId);
            logger.info("[PropertyController] Found {} properties for landlord ID: {}", properties.size(), landlordId);
            return ResponseEntity.ok(properties);
        } catch (SecurityException e) {
            logger.error("[PropertyController] Security exception: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @PostMapping(value = "/landlord/properties", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ROLE_LANDLORD')")
    public ResponseEntity<?> addProperty(
            @RequestPart("property") String propertyJson,
            @RequestPart(value = "images[]", required = false) List<MultipartFile> images,
            @RequestPart(value = "primaryImageIndex", required = false) String primaryImageIndexStr) {
        logger.info("[PropertyController] Received addProperty request");
        logger.info("[PropertyController] propertyJson: {}", propertyJson);
        logger.info("[PropertyController] images count: {}", images != null ? images.size() : 0);
        logger.info("[PropertyController] primaryImageIndex: {}", primaryImageIndexStr);
        try {
            String landlordId = authUtils.getCurrentUserId();
            if (landlordId == null) {
                logger.warn("[PropertyController] Unauthorized: No landlordId found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            PropertyDTO propertyDTO = objectMapper.readValue(propertyJson, PropertyDTO.class);
            Property property = new Property();
            property.setTitle(propertyDTO.getTitle());
            property.setDescription(propertyDTO.getDescription());
            property.setBedrooms(propertyDTO.getBedrooms());
            property.setBathrooms(propertyDTO.getBathrooms());
            property.setAddress(propertyDTO.getLocation());
            property.setLocation(propertyDTO.getLocation());
            property.setRent(propertyDTO.getPrice());
            property.setCurrency(propertyDTO.getCurrency());
            property.setSquareFeet(propertyDTO.getSquareFeet());
            property.setBuiltYear(propertyDTO.getBuiltYear());
            property.setAvailableFrom(propertyDTO.getAvailableFrom());
            property.setUtilitiesIncluded(propertyDTO.getUtilitiesIncluded());
            property.setAmenities(propertyDTO.getAmenities());
            property.setStatus(propertyDTO.getStatus());
            property.setPropertyType(propertyDTO.getPropertyType());
            property.setIsSharedBedrooms(propertyDTO.getIsSharedBedrooms());
            property.setIsSharedBathrooms(propertyDTO.getIsSharedBathrooms());
            property.setRating(propertyDTO.getRating());
            property.setLandlordId(landlordId);

            Integer primaryImageIndex = parsePrimaryImageIndex(primaryImageIndexStr);
            List<MultipartFile> imageList = (images != null) ? images : Collections.emptyList();
            Property savedProperty = propertyService.createPropertyWithLandlordId(
                    property, imageList, landlordId, primaryImageIndex);
            logger.info("[PropertyController] Property created with ID: {}", savedProperty.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProperty);
        } catch (IllegalArgumentException e) {
            logger.error("[PropertyController] Invalid input: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            logger.error("[PropertyController] Error adding property: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding property: " + e.getMessage());
        }
    }

    @PutMapping(value = "/landlord/properties/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ROLE_LANDLORD')")
    public ResponseEntity<?> updateProperty(
            @PathVariable String id,
            @RequestPart("property") String propertyJson,
            @RequestPart(value = "images[]", required = false) List<MultipartFile> images,
            @RequestPart(value = "primaryImageIndex", required = false) String primaryImageIndexStr,
            @RequestPart(value = "removedImages", required = false) String removedImagesJson) {
        logger.info("[PropertyController] Received updateProperty request for ID: {}", id);
        logger.info("[PropertyController] propertyJson: {}", propertyJson);
        logger.info("[PropertyController] images count: {}", images != null ? images.size() : 0);
        logger.info("[PropertyController] primaryImageIndex: {}", primaryImageIndexStr);
        logger.info("[PropertyController] removedImagesJson: {}", removedImagesJson);
        try {
            if (!isValidObjectId(id)) {
                logger.warn("[PropertyController] Invalid ID format: {}", id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid property ID format");
            }
            String landlordId = authUtils.getCurrentUserId();
            if (landlordId == null) {
                logger.warn("[PropertyController] Unauthorized: No landlordId found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: No landlord ID found");
            }
            PropertyDTO propertyDTO = objectMapper.readValue(propertyJson, PropertyDTO.class);
            Property property = new Property();
            property.setId(id);
            property.setTitle(propertyDTO.getTitle());
            property.setDescription(propertyDTO.getDescription());
            property.setBedrooms(propertyDTO.getBedrooms());
            property.setBathrooms(propertyDTO.getBathrooms());
            property.setAddress(propertyDTO.getLocation());
            property.setLocation(propertyDTO.getLocation());
            property.setRent(propertyDTO.getPrice());
            property.setCurrency(propertyDTO.getCurrency());
            property.setSquareFeet(propertyDTO.getSquareFeet());
            property.setBuiltYear(propertyDTO.getBuiltYear());
            property.setAvailableFrom(propertyDTO.getAvailableFrom());
            property.setUtilitiesIncluded(propertyDTO.getUtilitiesIncluded());
            property.setAmenities(propertyDTO.getAmenities());
            property.setStatus(propertyDTO.getStatus());
            property.setPropertyType(propertyDTO.getPropertyType());
            property.setIsSharedBedrooms(propertyDTO.getIsSharedBedrooms());
            property.setIsSharedBathrooms(propertyDTO.getIsSharedBathrooms());
            property.setRating(propertyDTO.getRating());
            property.setLandlordId(landlordId);

            Integer primaryImageIndex = parsePrimaryImageIndex(primaryImageIndexStr);
            List<String> removedImages = parseRemovedImages(removedImagesJson);
            List<MultipartFile> imageList = (images != null) ? images : Collections.emptyList();
            Property updatedProperty = propertyService.updateProperty(
                    property, imageList, landlordId, primaryImageIndex, removedImages);
            logger.info("[PropertyController] Property updated with ID: {}", updatedProperty.getId());
            return ResponseEntity.ok(updatedProperty);
        } catch (IllegalArgumentException e) {
            logger.error("[PropertyController] Invalid input: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input: " + e.getMessage());
        } catch (SecurityException e) {
            logger.error("[PropertyController] Forbidden: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden: " + e.getMessage());
        } catch (Exception e) {
            logger.error("[PropertyController] Error updating property: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating property: " + e.getMessage());
        }
    }

    @DeleteMapping("/landlord/properties/{id}")
    @PreAuthorize("hasAuthority('ROLE_LANDLORD')")
    public ResponseEntity<?> deleteLandlordProperty(@PathVariable String id) {
        logger.info("[PropertyController] Received deleteLandlordProperty request for ID: {}", id);
        try {
            if (!isValidObjectId(id)) {
                logger.warn("[PropertyController] Invalid ID format: {}", id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid property ID format");
            }
            String landlordId = authUtils.getCurrentUserId();
            if (landlordId == null) {
                logger.warn("[PropertyController] Unauthorized: No landlordId found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: No landlord ID found");
            }
            propertyService.deleteLandlordProperty(id, landlordId);
            logger.info("[PropertyController] Property deleted with ID: {}", id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            logger.error("[PropertyController] Invalid input: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input: " + e.getMessage());
        } catch (SecurityException e) {
            logger.error("[PropertyController] Forbidden: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden: " + e.getMessage());
        } catch (Exception e) {
            logger.error("[PropertyController] Error deleting property: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting property: " + e.getMessage());
        }
    }

    private boolean isValidObjectId(String id) {
        return id != null && id.matches("^[0-9a-fA-F]{24}$");
    }

    private Integer parsePrimaryImageIndex(String primaryImageIndexStr) {
        if (primaryImageIndexStr != null && !primaryImageIndexStr.trim().isEmpty()) {
            try {
                return Integer.parseInt(primaryImageIndexStr);
            } catch (NumberFormatException e) {
                logger.error("[PropertyController] Invalid primaryImageIndex: {}", primaryImageIndexStr);
                throw new IllegalArgumentException("Invalid primary image index format");
            }
        }
        return null;
    }

    private List<String> parseRemovedImages(String removedImagesJson) {
        if (removedImagesJson != null && !removedImagesJson.trim().isEmpty()) {
            try {
                return objectMapper.readValue(removedImagesJson, List.class);
            } catch (Exception e) {
                logger.error("[PropertyController] Invalid removedImages format: {}", removedImagesJson);
                throw new IllegalArgumentException("Invalid removed images format");
            }
        }
        return Collections.emptyList();
    }
}