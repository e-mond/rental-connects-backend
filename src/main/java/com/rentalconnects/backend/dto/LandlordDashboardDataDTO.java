package com.rentalconnects.backend.dto;

/**
 * DTO for transferring landlord dashboard data to the frontend.
 * Contains key metrics for the landlord's dashboard, such as active rentals, revenue, and property statistics.
 */
public class LandlordDashboardDataDTO {

    private int totalProperties;    // Total number of properties owned by the landlord
    private int activeRentals;      // Number of currently active leases
    private double monthlyRevenue;  // Revenue for the current month
    private double averageRating;   // Average rating of the landlord's properties
    private int pendingIssues;      // Number of pending maintenance requests
    private int vacantProperties;   // Number of properties with status "Vacant"
    private int underMaintenance;   // Number of properties under maintenance

    // Constructor
    public LandlordDashboardDataDTO() {
    }

    // Getters and Setters
    public int getTotalProperties() {
        return totalProperties;
    }

    public void setTotalProperties(int totalProperties) {
        this.totalProperties = totalProperties;
    }

    public int getActiveRentals() {
        return activeRentals;
    }

    public void setActiveRentals(int activeRentals) {
        this.activeRentals = activeRentals;
    }

    public double getMonthlyRevenue() {
        return monthlyRevenue;
    }

    public void setMonthlyRevenue(double monthlyRevenue) {
        this.monthlyRevenue = monthlyRevenue;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getPendingIssues() {
        return pendingIssues;
    }

    public void setPendingIssues(int pendingIssues) {
        this.pendingIssues = pendingIssues;
    }

    public int getVacantProperties() {
        return vacantProperties;
    }

    public void setVacantProperties(int vacantProperties) {
        this.vacantProperties = vacantProperties;
    }

    public int getUnderMaintenance() {
        return underMaintenance;
    }

    public void setUnderMaintenance(int underMaintenance) {
        this.underMaintenance = underMaintenance;
    }
}