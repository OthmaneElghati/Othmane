package com.humanitaire.backend.service;

import com.humanitaire.backend.dto.DashboardStats;
import com.humanitaire.backend.entity.*;
import com.humanitaire.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final MissionRepository missionRepository;
    private final VolunteerRepository volunteerRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final DonationRepository donationRepository;
    private final ConvoyRepository convoyRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public DashboardStats getStats() {
        Map<String, Long> missionsByStatus = missionRepository.countByStatusGroup().stream()
                .collect(Collectors.toMap(row -> row[0].toString(), row -> (Long) row[1], (a, b) -> a, LinkedHashMap::new));

        Map<String, Long> missionsByRegion = missionRepository.countByRegion().stream()
                .collect(Collectors.toMap(row -> row[0] != null ? row[0].toString() : "N/A", row -> (Long) row[1], (a, b) -> a, LinkedHashMap::new));

        Map<String, Long> missionsByPriority = missionRepository.countByPriority().stream()
                .collect(Collectors.toMap(row -> row[0].toString(), row -> (Long) row[1], (a, b) -> a, LinkedHashMap::new));

        Map<String, Double> donationsByMonth = new LinkedHashMap<>();
        try {
            donationsByMonth = donationRepository.sumByMonth().stream()
                    .collect(Collectors.toMap(
                            row -> row[0] != null ? row[0].toString() : "N/A",
                            row -> row[1] != null ? ((Number) row[1]).doubleValue() : 0.0,
                            (a, b) -> a,
                            LinkedHashMap::new
                    ));
        } catch (Exception ignored) {
        }

        Map<String, Long> volunteersByRegion = volunteerRepository.countByRegion().stream()
                .collect(Collectors.toMap(row -> row[0] != null ? row[0].toString() : "N/A", row -> (Long) row[1], (a, b) -> a, LinkedHashMap::new));

        Map<String, Long> beneficiariesByEmergencyLevel = beneficiaryRepository.countByEmergencyLevel().stream()
                .collect(Collectors.toMap(row -> row[0].toString(), row -> (Long) row[1], (a, b) -> a, LinkedHashMap::new));

        List<Map<String, Object>> recentMissions = missionRepository.findTop5ByOrderByCreatedAtDesc().stream()
                .map(m -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("id", m.getId());
                    map.put("title", m.getTitle());
                    map.put("status", m.getStatus() != null ? m.getStatus().name() : null);
                    map.put("priority", m.getPriority() != null ? m.getPriority().name() : null);
                    map.put("city", m.getCity());
                    map.put("region", m.getRegion());
                    map.put("createdAt", m.getCreatedAt());
                    return map;
                }).toList();

        List<Map<String, Object>> recentDonations = donationRepository.findTop5ByOrderByCreatedAtDesc().stream()
                .map(d -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("id", d.getId());
                    map.put("donorName", d.getDonorName());
                    map.put("amount", d.getAmount());
                    map.put("currency", d.getCurrency());
                    map.put("status", d.getStatus() != null ? d.getStatus().name() : null);
                    map.put("createdAt", d.getCreatedAt());
                    return map;
                }).toList();

        List<Map<String, Object>> alerts = new ArrayList<>();
        long criticalMissions = missionRepository.countByStatus(Mission.MissionStatus.ACTIVE);
        if (criticalMissions > 10) {
            alerts.add(Map.of("type", "WARNING", "message", criticalMissions + " missions actives en cours"));
        }
        long delayedConvoys = convoyRepository.countByStatus(Convoy.ConvoyStatus.DELAYED);
        if (delayedConvoys > 0) {
            alerts.add(Map.of("type", "ERROR", "message", delayedConvoys + " convoi(s) en retard"));
        }

        List<Map<String, Object>> recommendations = new ArrayList<>();
        List<Mission> activeMissions = missionRepository.findActiveMissionsSorted();
        if (!activeMissions.isEmpty()) {
            Mission top = activeMissions.get(0);
            recommendations.add(Map.of("type", "MISSION", "message", "Priorité: " + top.getTitle(), "id", top.getId()));
        }

        Double totalDonationAmount = donationRepository.sumCompletedAmount();

        return DashboardStats.builder()
                .totalMissions(missionRepository.count())
                .activeMissions(missionRepository.countByStatus(Mission.MissionStatus.ACTIVE))
                .completedMissions(missionRepository.countByStatus(Mission.MissionStatus.COMPLETED))
                .plannedMissions(missionRepository.countByStatus(Mission.MissionStatus.PLANNED))
                .totalVolunteers(volunteerRepository.count())
                .availableVolunteers(volunteerRepository.countByAvailableTrue())
                .totalBeneficiaries(beneficiaryRepository.count())
                .totalDonations(donationRepository.count())
                .totalDonationAmount(totalDonationAmount != null ? totalDonationAmount : 0.0)
                .totalConvoys(convoyRepository.count())
                .activeConvoys(convoyRepository.countByStatus(Convoy.ConvoyStatus.IN_TRANSIT))
                .totalEvents(eventRepository.count())
                .totalUsers(userRepository.count())
                .missionsByStatus(missionsByStatus)
                .missionsByRegion(missionsByRegion)
                .missionsByPriority(missionsByPriority)
                .donationsByMonth(donationsByMonth)
                .volunteersByRegion(volunteersByRegion)
                .beneficiariesByEmergencyLevel(beneficiariesByEmergencyLevel)
                .recentMissions(recentMissions)
                .recentDonations(recentDonations)
                .recentActivities(new ArrayList<>())
                .alerts(alerts)
                .recommendations(recommendations)
                .build();
    }
}
