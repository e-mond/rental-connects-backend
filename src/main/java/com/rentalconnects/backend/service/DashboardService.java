package com.rentalconnects.backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.OptionalDouble;

import org.springframework.stereotype.Service;

import com.rentalconnects.backend.dto.LandlordDashboardDataDTO;
import com.rentalconnects.backend.model.Lease;
import com.rentalconnects.backend.model.MaintenanceRequest;
import com.rentalconnects.backend.model.Payment;
import com.rentalconnects.backend.model.Property;
import com.rentalconnects.backend.repository.LeaseRepository;
import com.rentalconnects.backend.repository.MaintenanceRequestRepository;
import com.rentalconnects.backend.repository.PaymentRepository;
import com.rentalconnects.backend.repository.PropertyRepository;

/**
 * Service for generating dashboard data for landlords.
 * Provides aggregated data such as total properties, active rentals, monthly revenue, average ratings,
 * pending maintenance issues, vacant properties, and properties under maintenance.
 */
@Service
public class DashboardService {

    private final PropertyRepository propertyRepository;
    private final LeaseRepository leaseRepository;
    private final PaymentRepository paymentRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;

    /**
     * Constructor for DashboardService.
     *
     * @param propertyRepository Repository for property data.
     * @param leaseRepository Repository for lease data.
     * @param paymentRepository Repository for payment data.
     * @param maintenanceRequestRepository Repository for maintenance request data.
     */
    public DashboardService(PropertyRepository propertyRepository,
                            LeaseRepository leaseRepository,
                            PaymentRepository paymentRepository,
                            MaintenanceRequestRepository maintenanceRequestRepository) {
        this.propertyRepository = propertyRepository;
        this.leaseRepository = leaseRepository;
        this.paymentRepository = paymentRepository;
        this.maintenanceRequestRepository = maintenanceRequestRepository;
    }

    /**
     * Generates dashboard data for a landlord.
     *
     * @param landlordId The ID of the landlord.
     * @return LandlordDashboardDataDTO containing the landlord's dashboard data.
     */
    public LandlordDashboardDataDTO getLandlordDashboardData(String landlordId) {
        LandlordDashboardDataDTO dashboardData = new LandlordDashboardDataDTO();

        // Total Properties
        List<Property> properties = propertyRepository.findByLandlordId(landlordId);
        dashboardData.setTotalProperties(properties.size());

        // Active Rentals
        List<Lease> leases = leaseRepository.findByLandlordId(landlordId);
        long activeRentals = leases.stream()
                .filter(lease -> {
                    LocalDate today = LocalDate.now();
                    LocalDate startDate = lease.getStartDate();
                    LocalDate endDate = lease.getEndDate();
                    return startDate != null && endDate != null &&
                           !today.isBefore(startDate) && !today.isAfter(endDate);
                })
                .count();
        dashboardData.setActiveRentals((int) activeRentals);

        // Monthly Revenue
        YearMonth currentMonth = YearMonth.now();
        LocalDate startOfMonth = currentMonth.atDay(1);
        LocalDate endOfMonth = currentMonth.atEndOfMonth();
        List<Payment> payments = paymentRepository.findByLandlordId(landlordId);
        double monthlyRevenue = payments.stream()
                .filter(payment -> {
                    LocalDateTime paymentDate = payment.getPaymentDate();
                    if (paymentDate != null) {
                        LocalDate paymentLocalDate = paymentDate.toLocalDate();
                        return !paymentLocalDate.isBefore(startOfMonth) && !paymentLocalDate.isAfter(endOfMonth);
                    }
                    return false;
                })
                .mapToDouble(Payment::getAmount)
                .sum();
        dashboardData.setMonthlyRevenue(monthlyRevenue);

        // Average Rating
        OptionalDouble averageRating = properties.stream()
                .filter(property -> property.getRating() != null)
                .mapToDouble(Property::getRating)
                .average();
        dashboardData.setAverageRating(averageRating.orElse(0.0));

        // Pending Issues
        List<MaintenanceRequest> pendingIssues = maintenanceRequestRepository.findByLandlordIdAndStatus(landlordId, "PENDING");
        dashboardData.setPendingIssues(pendingIssues.size());

        // Vacant Properties
        long vacantProperties = properties.stream()
                .filter(property -> "VACANT".equals(property.getStatus()))
                .count();
        dashboardData.setVacantProperties((int) vacantProperties);

        // Under Maintenance
        long underMaintenance = properties.stream()
                .filter(property -> "MAINTENANCE".equals(property.getStatus()))
                .count();
        dashboardData.setUnderMaintenance((int) underMaintenance);

        return dashboardData;
    }
}