package com.humanitaire.backend.service;

import com.humanitaire.backend.dto.MissionDTO;
import com.humanitaire.backend.entity.Mission;
import com.humanitaire.backend.entity.Volunteer;
import com.humanitaire.backend.exception.ResourceNotFoundException;
import com.humanitaire.backend.repository.MissionRepository;
import com.humanitaire.backend.repository.VolunteerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;
    private final VolunteerRepository volunteerRepository;

    public Page<MissionDTO> getAll(Pageable pageable) {
        return missionRepository.findAll(pageable).map(this::toDTO);
    }

    public MissionDTO getById(Long id) {
        return toDTO(findById(id));
    }

    public Page<MissionDTO> search(String query, Pageable pageable) {
        return missionRepository.search(query, pageable).map(this::toDTO);
    }

    public Page<MissionDTO> getByStatus(String status, Pageable pageable) {
        return missionRepository.findByStatus(Mission.MissionStatus.valueOf(status), pageable).map(this::toDTO);
    }

    public Page<MissionDTO> getByRegion(String region, Pageable pageable) {
        return missionRepository.findByRegion(region, pageable).map(this::toDTO);
    }

    public Page<MissionDTO> getByPriority(String priority, Pageable pageable) {
        return missionRepository.findByPriority(Mission.MissionPriority.valueOf(priority), pageable).map(this::toDTO);
    }

    public Page<MissionDTO> getByManager(Long managerId, Pageable pageable) {
        return missionRepository.findByManagerId(managerId, pageable).map(this::toDTO);
    }

    @Transactional
    public MissionDTO create(MissionDTO dto) {
        Mission mission = toEntity(dto);
        mission.setPriorityScore(calculatePriorityScore(mission));
        return toDTO(missionRepository.save(mission));
    }

    @Transactional
    public MissionDTO update(Long id, MissionDTO dto) {
        Mission mission = findById(id);
        mission.setTitle(dto.getTitle());
        mission.setDescription(dto.getDescription());
        mission.setStartDate(dto.getStartDate());
        mission.setEndDate(dto.getEndDate());
        mission.setBudget(dto.getBudget());
        if (dto.getStatus() != null) mission.setStatus(Mission.MissionStatus.valueOf(dto.getStatus()));
        if (dto.getPriority() != null) mission.setPriority(Mission.MissionPriority.valueOf(dto.getPriority()));
        mission.setLatitude(dto.getLatitude());
        mission.setLongitude(dto.getLongitude());
        mission.setCity(dto.getCity());
        mission.setRegion(dto.getRegion());
        mission.setNumberOfBeneficiaries(dto.getNumberOfBeneficiaries());
        mission.setPriorityScore(calculatePriorityScore(mission));
        return toDTO(missionRepository.save(mission));
    }

    @Transactional
    public void delete(Long id) {
        Mission mission = findById(id);
        mission.getVolunteers().clear();
        missionRepository.save(mission);
        missionRepository.deleteById(id);
    }

    @Transactional
    public MissionDTO assignVolunteer(Long missionId, Long volunteerId) {
        Mission mission = findById(missionId);
        Volunteer volunteer = volunteerRepository.findById(volunteerId)
                .orElseThrow(() -> new ResourceNotFoundException("Bénévole non trouvé"));
        mission.getVolunteers().add(volunteer);
        return toDTO(missionRepository.save(mission));
    }

    @Transactional
    public MissionDTO removeVolunteer(Long missionId, Long volunteerId) {
        Mission mission = findById(missionId);
        mission.getVolunteers().removeIf(v -> v.getId().equals(volunteerId));
        return toDTO(missionRepository.save(mission));
    }

    public int calculatePriorityScore(Mission mission) {
        int score = 0;
        if (mission.getPriority() != null) {
            switch (mission.getPriority()) {
                case CRITICAL -> score += 40;
                case HIGH -> score += 30;
                case MEDIUM -> score += 20;
                case LOW -> score += 10;
            }
        }
        if (mission.getNumberOfBeneficiaries() != null) {
            if (mission.getNumberOfBeneficiaries() > 500) score += 20;
            else if (mission.getNumberOfBeneficiaries() > 200) score += 15;
            else if (mission.getNumberOfBeneficiaries() > 50) score += 10;
            else score += 5;
        }
        if (mission.getBudget() != null && mission.getBudget() > 0) {
            if (mission.getBudget() < 50000) score += 10;
            else if (mission.getBudget() < 100000) score += 5;
        }
        if (mission.getEndDate() != null) {
            long daysUntilEnd = ChronoUnit.DAYS.between(LocalDate.now(), mission.getEndDate());
            if (daysUntilEnd < 7) score += 15;
            else if (daysUntilEnd < 30) score += 10;
            else if (daysUntilEnd < 90) score += 5;
        }
        int volunteerCount = mission.getVolunteers() != null ? mission.getVolunteers().size() : 0;
        if (volunteerCount == 0) score += 10;
        else if (volunteerCount < 3) score += 5;
        return Math.min(score, 100);
    }

    public List<MissionDTO> getRecommendedMissions() {
        return missionRepository.findActiveMissionsSorted().stream()
                .limit(5)
                .map(this::toDTO)
                .toList();
    }

    private Mission findById(Long id) {
        return missionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mission non trouvée avec l'id: " + id));
    }

    private MissionDTO toDTO(Mission m) {
        return MissionDTO.builder()
                .id(m.getId())
                .title(m.getTitle())
                .description(m.getDescription())
                .startDate(m.getStartDate())
                .endDate(m.getEndDate())
                .budget(m.getBudget())
                .status(m.getStatus() != null ? m.getStatus().name() : null)
                .priority(m.getPriority() != null ? m.getPriority().name() : null)
                .latitude(m.getLatitude())
                .longitude(m.getLongitude())
                .city(m.getCity())
                .region(m.getRegion())
                .image(m.getImage())
                .numberOfBeneficiaries(m.getNumberOfBeneficiaries())
                .priorityScore(m.getPriorityScore())
                .managerId(m.getManager() != null ? m.getManager().getId() : null)
                .managerName(m.getManager() != null ? m.getManager().getFullName() : null)
                .volunteerCount(m.getVolunteers() != null ? m.getVolunteers().size() : 0)
                .beneficiaryCount(m.getBeneficiaries() != null ? m.getBeneficiaries().size() : 0)
                .createdAt(m.getCreatedAt())
                .updatedAt(m.getUpdatedAt())
                .build();
    }

    private Mission toEntity(MissionDTO dto) {
        return Mission.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .budget(dto.getBudget())
                .status(dto.getStatus() != null ? Mission.MissionStatus.valueOf(dto.getStatus()) : Mission.MissionStatus.PLANNED)
                .priority(dto.getPriority() != null ? Mission.MissionPriority.valueOf(dto.getPriority()) : Mission.MissionPriority.MEDIUM)
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .city(dto.getCity())
                .region(dto.getRegion())
                .numberOfBeneficiaries(dto.getNumberOfBeneficiaries())
                .build();
    }
}
