package com.humanitaire.backend.dto;

import lombok.*;
import java.util.List;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DashboardStats {
    private long totalMissions;
    private long activeMissions;
    private long completedMissions;
    private long plannedMissions;
    private long totalVolunteers;
    private long availableVolunteers;
    private long totalBeneficiaries;
    private long totalDonations;
    private double totalDonationAmount;
    private long totalConvoys;
    private long activeConvoys;
    private long totalEvents;
    private long totalUsers;
    private Map<String, Long> missionsByStatus;
    private Map<String, Long> missionsByRegion;
    private Map<String, Long> missionsByPriority;
    private Map<String, Double> donationsByMonth;
    private Map<String, Long> volunteersByRegion;
    private Map<String, Long> beneficiariesByEmergencyLevel;
    private List<Map<String, Object>> recentMissions;
    private List<Map<String, Object>> recentDonations;
    private List<Map<String, Object>> recentActivities;
    private List<Map<String, Object>> alerts;
    private List<Map<String, Object>> recommendations;
}
