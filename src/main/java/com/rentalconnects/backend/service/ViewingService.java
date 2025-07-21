package com.rentalconnects.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.rentalconnects.backend.model.Viewing;
import com.rentalconnects.backend.repository.ViewingRepository;

@Service
public class ViewingService {

    private final ViewingRepository viewingRepository;
    private final PropertyService propertyService;

    public ViewingService(ViewingRepository viewingRepository, PropertyService propertyService) {
        this.viewingRepository = viewingRepository;
        this.propertyService = propertyService;
    }

    public Viewing scheduleViewing(String propertyId, String tenantId, LocalDateTime viewingDate, String notes, boolean important) {
        String propertyName = propertyService.getPropertyNameById(propertyId);
        if (propertyName == null) {
            throw new RuntimeException("Property not found for ID: " + propertyId);
        }
        Viewing viewing = new Viewing();
        viewing.setPropertyId(propertyId);
        viewing.setPropertyName(propertyName);
        viewing.setTenantId(tenantId);
        viewing.setViewingDate(viewingDate);
        viewing.setStatus("SCHEDULED");
        viewing.setNotes(notes);
        viewing.setImportant(important);
        viewing.setScheduledTime(LocalDateTime.now());
        viewing.setUserId(tenantId);
        return viewingRepository.save(viewing);
    }

    public Viewing rescheduleViewing(String viewingId, String tenantId, LocalDateTime viewingDate, String notes, boolean important) {
        Viewing viewing = viewingRepository.findById(viewingId)
            .orElseThrow(() -> new RuntimeException("Viewing not found"));
        if (!viewing.getTenantId().equals(tenantId)) {
            throw new RuntimeException("Unauthorized to reschedule this viewing");
        }
        String propertyName = propertyService.getPropertyNameById(viewing.getPropertyId());
        if (propertyName == null) {
            throw new RuntimeException("Property not found for ID: " + viewing.getPropertyId());
        }
        viewing.setPropertyName(propertyName);
        viewing.setViewingDate(viewingDate);
        viewing.setNotes(notes);
        viewing.setImportant(important);
        viewing.setStatus("SCHEDULED");
        return viewingRepository.save(viewing);
    }

    public Viewing cancelViewing(String viewingId, String tenantId) {
        Viewing viewing = viewingRepository.findById(viewingId)
            .orElseThrow(() -> new RuntimeException("Viewing not found"));
        if (!viewing.getTenantId().equals(tenantId)) {
            throw new RuntimeException("Unauthorized to cancel this viewing");
        }
        viewing.setStatus("CANCELLED");
        return viewingRepository.save(viewing);
    }

    public Viewing getViewingById(String viewingId, String tenantId) {
        Viewing viewing = viewingRepository.findById(viewingId)
            .orElseThrow(() -> new RuntimeException("Viewing not found"));
        if (!viewing.getTenantId().equals(tenantId)) {
            throw new RuntimeException("Unauthorized to view this viewing");
        }
        return viewing;
    }

    public List<Viewing> getViewingsByPropertyId(String propertyId) {
        return viewingRepository.findByPropertyId(propertyId);
    }

    public List<Viewing> getViewingsByTenantId(String tenantId) {
        return viewingRepository.findByTenantId(tenantId);
    }
}