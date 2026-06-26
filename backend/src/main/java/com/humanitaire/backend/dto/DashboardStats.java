package com.humanitaire.backend.dto;

import lombok.*;
import java.util.List;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DashboardStats {
    private long totalMissions;
    private long activeMissions;
    private long completedMissions;
    private long totalVolunteers;
    private long totalBeneficiaries;
    private long totalDonations;
    private double totalDonationAmount;
    private long totalEvents;
    private long totalConvoys;
    private List<Map<String, Object>> monthlyDonations;
    private List<Map<String, Object>> missionsByStatus;
    private List<Map<String, Object>> missionsByRegion;
    private List<Map<String, Object>> volunteerGrowth;
    private List<Map<String, Object>> beneficiaryDistribution;
}
