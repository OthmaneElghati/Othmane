package com.humanitaire.backend.service;

import com.humanitaire.backend.dto.DashboardStats;
import com.humanitaire.backend.entity.Mission;
import com.humanitaire.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final MissionRepository missionRepository;
    private final VolunteerRepository volunteerRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final DonationRepository donationRepository;
    private final EventRepository eventRepository;
    private final ConvoyRepository convoyRepository;

    public DashboardStats getStats() {
        Double totalAmount = donationRepository.getTotalDonationAmount();

        List<Map<String, Object>> monthlyDonations = new ArrayList<>();
        String[] months = {"Jan", "Fév", "Mar", "Avr", "Mai", "Jun", "Jul", "Aoû", "Sep", "Oct", "Nov", "Déc"};
        List<Object[]> donationData = donationRepository.getMonthlyDonations();
        for (Object[] row : donationData) {
            Map<String, Object> item = new HashMap<>();
            int monthIdx = ((Number) row[0]).intValue() - 1;
            item.put("month", months[monthIdx]);
            item.put("amount", row[1]);
            monthlyDonations.add(item);
        }

        List<Map<String, Object>> missionsByStatus = new ArrayList<>();
        List<Object[]> statusData = missionRepository.countByStatusGroup();
        for (Object[] row : statusData) {
            Map<String, Object> item = new HashMap<>();
            item.put("status", row[0].toString());
            item.put("count", row[1]);
            missionsByStatus.add(item);
        }

        List<Map<String, Object>> missionsByRegion = new ArrayList<>();
        List<Object[]> regionData = missionRepository.countByRegion();
        for (Object[] row : regionData) {
            Map<String, Object> item = new HashMap<>();
            item.put("region", row[0]);
            item.put("count", row[1]);
            missionsByRegion.add(item);
        }

        List<Map<String, Object>> volunteerGrowth = new ArrayList<>();
        List<Object[]> volData = volunteerRepository.countByMonth();
        for (Object[] row : volData) {
            Map<String, Object> item = new HashMap<>();
            int monthIdx = ((Number) row[0]).intValue() - 1;
            item.put("month", months[monthIdx]);
            item.put("count", row[1]);
            volunteerGrowth.add(item);
        }

        List<Map<String, Object>> beneficiaryDistribution = new ArrayList<>();
        List<Object[]> benData = beneficiaryRepository.countByRegion();
        for (Object[] row : benData) {
            Map<String, Object> item = new HashMap<>();
            item.put("region", row[0]);
            item.put("count", row[1]);
            beneficiaryDistribution.add(item);
        }

        return DashboardStats.builder()
                .totalMissions(missionRepository.count())
                .activeMissions(missionRepository.countByStatus(Mission.MissionStatus.ACTIVE))
                .completedMissions(missionRepository.countByStatus(Mission.MissionStatus.COMPLETED))
                .totalVolunteers(volunteerRepository.count())
                .totalBeneficiaries(beneficiaryRepository.count())
                .totalDonations(donationRepository.count())
                .totalDonationAmount(totalAmount != null ? totalAmount : 0.0)
                .totalEvents(eventRepository.count())
                .totalConvoys(convoyRepository.count())
                .monthlyDonations(monthlyDonations)
                .missionsByStatus(missionsByStatus)
                .missionsByRegion(missionsByRegion)
                .volunteerGrowth(volunteerGrowth)
                .beneficiaryDistribution(beneficiaryDistribution)
                .build();
    }
}
