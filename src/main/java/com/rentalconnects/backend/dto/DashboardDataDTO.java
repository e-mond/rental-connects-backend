package com.rentalconnects.backend.dto;

import com.rentalconnects.backend.model.Activity;
import com.rentalconnects.backend.model.Payment;
import com.rentalconnects.backend.model.RentalApplication;

import java.util.List;

/**
 * DTO for transferring tenant dashboard data to the frontend.
 * Contains key information for the tenant's dashboard, such as leases, payments, activities, and applications.
 */
public class DashboardDataDTO {

    private List<LeaseDTO> leases;              // List of the tenant's active leases
    private List<Payment> recentPayments;       // List of recent payments made by the tenant
    private List<Activity> recentActivities;    // List of recent activities related to the tenant
    private List<RentalApplication> applications; // List of the tenant's rental applications

    // Constructor
    public DashboardDataDTO() {
    }

    // Getters and Setters
    public List<LeaseDTO> getLeases() {
        return leases;
    }

    public void setLeases(List<LeaseDTO> leases) {
        this.leases = leases;
    }

    public List<Payment> getRecentPayments() {
        return recentPayments;
    }

    public void setRecentPayments(List<Payment> recentPayments) {
        this.recentPayments = recentPayments;
    }

    public List<Activity> getRecentActivities() {
        return recentActivities;
    }

    public void setRecentActivities(List<Activity> recentActivities) {
        this.recentActivities = recentActivities;
    }

    public List<RentalApplication> getApplications() {
        return applications;
    }

    public void setApplications(List<RentalApplication> applications) {
        this.applications = applications;
    }
}