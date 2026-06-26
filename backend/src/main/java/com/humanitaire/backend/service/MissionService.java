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

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;
    private final VolunteerRepository volunteerRepository;

    public Page<MissionDTO> getAllMissions(Pageable pageable) {
        return missionRepository.findAll(pageable).map(this::toDTO);
    }

    public Page<MissionDTO> searchMissions(String query, Pageable pageable) {
        return missionRepository.findByTitleContainingIgnoreCase(query, pageable).map(this::toDTO);
    }

    public Page<MissionDTO> getMissionsByStatus(String status, Pageable pageable) {
        Mission.MissionStatus missionStatus = Mission.MissionStatus.valueOf(status.toUpperCase());
        return missionRepository.findByStatus(missionStatus, pageable).map(this::toDTO);
    }

    public Page<MissionDTO> getMissionsByRegion(String region, Pageable pageable) {
        return missionRepository.findByRegion(region, pageable).map(this::toDTO);
    }

    public MissionDTO getMissionById(Long id) {
        Mission mission = missionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mission", "id", id));
        return toDTO(mission);
    }

    @Transactional
    public MissionDTO createMission(MissionDTO dto) {
        Mission mission = toEntity(dto);
        mission.setPriorityScore(calculatePriorityScore(mission));
        mission = missionRepository.save(mission);
        return toDTO(mission);
    }

    @Transactional
    public MissionDTO updateMission(Long id, MissionDTO dto) {
        Mission mission = missionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mission", "id", id));

        mission.setTitle(dto.getTitle());
        mission.setDescription(dto.getDescription());
        mission.setStartDate(dto.getStartDate());
        mission.setEndDate(dto.getEndDate());
        mission.setBudget(dto.getBudget());
        mission.setStatus(Mission.MissionStatus.valueOf(dto.getStatus()));
        mission.setPriority(Mission.MissionPriority.valueOf(dto.getPriority()));
        mission.setLatitude(dto.getLatitude());
        mission.setLongitude(dto.getLongitude());
        mission.setCity(dto.getCity());
        mission.setRegion(dto.getRegion());
        mission.setImage(dto.getImage());
        mission.setPriorityScore(calculatePriorityScore(mission));

        mission = missionRepository.save(mission);
        return toDTO(mission);
    }

    public void deleteMission(Long id) {
        if (!missionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Mission", "id", id);
        }
        missionRepository.deleteById(id);
    }

    @Transactional
    public MissionDTO assignVolunteer(Long missionId, Long volunteerId) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new ResourceNotFoundException("Mission", "id", missionId));
        Volunteer volunteer = volunteerRepository.findById(volunteerId)
                .orElseThrow(() -> new ResourceNotFoundException("Bénévole", "id", volunteerId));

        mission.getVolunteers().add(volunteer);
        mission = missionRepository.save(mission);
        return toDTO(mission);
    }

    @Transactional
    public MissionDTO removeVolunteer(Long missionId, Long volunteerId) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new ResourceNotFoundException("Mission", "id", missionId));

        mission.getVolunteers().removeIf(v -> v.getId().equals(volunteerId));
        mission = missionRepository.save(mission);
        return toDTO(mission);
    }

    private int calculatePriorityScore(Mission mission) {
        int score = 0;
        if (mission.getPriority() != null) {
            switch (mission.getPriority()) {
                case CRITICAL -> score += 40;
                case HIGH -> score += 30;
                case MEDIUM -> score += 20;
                case LOW -> score += 10;
            }
        }
        if (mission.getBeneficiaries() != null) {
            score += Math.min(mission.getBeneficiaries().size() * 2, 30);
        }
        if (mission.getBudget() != null && mission.getBudget() > 0) {
            score += 10;
        }
        if (mission.getVolunteers() != null && mission.getVolunteers().size() < 3) {
            score += 20;
        }
        return Math.min(score, 100);
    }

    private MissionDTO toDTO(Mission mission) {
        return MissionDTO.builder()
                .id(mission.getId())
                .title(mission.getTitle())
                .description(mission.getDescription())
                .startDate(mission.getStartDate())
                .endDate(mission.getEndDate())
                .budget(mission.getBudget())
                .status(mission.getStatus() != null ? mission.getStatus().name() : null)
                .priority(mission.getPriority() != null ? mission.getPriority().name() : null)
                .latitude(mission.getLatitude())
                .longitude(mission.getLongitude())
                .city(mission.getCity())
                .region(mission.getRegion())
                .image(mission.getImage())
                .priorityScore(mission.getPriorityScore())
                .volunteerCount(mission.getVolunteers() != null ? mission.getVolunteers().size() : 0)
                .beneficiaryCount(mission.getBeneficiaries() != null ? mission.getBeneficiaries().size() : 0)
                .volunteerIds(mission.getVolunteers() != null ? mission.getVolunteers().stream().map(Volunteer::getId).collect(Collectors.toSet()) : Set.of())
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
                .image(dto.getImage())
                .build();
    }
}
