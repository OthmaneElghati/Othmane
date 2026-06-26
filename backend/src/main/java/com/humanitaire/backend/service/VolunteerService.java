package com.humanitaire.backend.service;

import com.humanitaire.backend.dto.VolunteerDTO;
import com.humanitaire.backend.entity.Mission;
import com.humanitaire.backend.entity.Volunteer;
import com.humanitaire.backend.exception.ResourceNotFoundException;
import com.humanitaire.backend.repository.VolunteerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VolunteerService {

    private final VolunteerRepository volunteerRepository;

    public Page<VolunteerDTO> getAll(Pageable pageable) {
        return volunteerRepository.findAll(pageable).map(this::toDTO);
    }

    public VolunteerDTO getById(Long id) {
        return toDTO(findById(id));
    }

    public Page<VolunteerDTO> search(String query, Pageable pageable) {
        return volunteerRepository.search(query, pageable).map(this::toDTO);
    }

    public Page<VolunteerDTO> getAvailable(Pageable pageable) {
        return volunteerRepository.findByAvailableTrue(pageable).map(this::toDTO);
    }

    public Page<VolunteerDTO> getByRegion(String region, Pageable pageable) {
        return volunteerRepository.findByRegion(region, pageable).map(this::toDTO);
    }

    public VolunteerDTO getByUserId(Long userId) {
        return volunteerRepository.findByUserId(userId).map(this::toDTO).orElse(null);
    }

    @Transactional
    public VolunteerDTO create(VolunteerDTO dto) {
        return toDTO(volunteerRepository.save(toEntity(dto)));
    }

    @Transactional
    public VolunteerDTO update(Long id, VolunteerDTO dto) {
        Volunteer vol = findById(id);
        vol.setFirstName(dto.getFirstName());
        vol.setLastName(dto.getLastName());
        vol.setEmail(dto.getEmail());
        vol.setPhone(dto.getPhone());
        vol.setAddress(dto.getAddress());
        vol.setCity(dto.getCity());
        vol.setRegion(dto.getRegion());
        vol.setSkills(dto.getSkills());
        vol.setAvailable(dto.isAvailable());
        vol.setExperience(dto.getExperience());
        return toDTO(volunteerRepository.save(vol));
    }

    @Transactional
    public void delete(Long id) {
        Volunteer vol = findById(id);
        for (Mission m : vol.getMissions()) {
            m.getVolunteers().remove(vol);
        }
        vol.getMissions().clear();
        volunteerRepository.delete(vol);
    }

    public List<VolunteerDTO> recommendForMission(String region, String requiredSkills) {
        List<Volunteer> candidates = volunteerRepository.findAvailableByRegion(region);
        if (requiredSkills != null && !requiredSkills.isEmpty()) {
            String[] skills = requiredSkills.split(",");
            candidates = candidates.stream()
                    .sorted((a, b) -> {
                        int scoreA = calculateMatchScore(a, skills, region);
                        int scoreB = calculateMatchScore(b, skills, region);
                        return Integer.compare(scoreB, scoreA);
                    })
                    .collect(Collectors.toList());
        }
        return candidates.stream().limit(10).map(this::toDTO).toList();
    }

    private int calculateMatchScore(Volunteer v, String[] requiredSkills, String region) {
        int score = 0;
        if (v.getSkills() != null) {
            for (String skill : requiredSkills) {
                if (v.getSkills().toLowerCase().contains(skill.trim().toLowerCase())) {
                    score += 20;
                }
            }
        }
        if (v.getRegion() != null && v.getRegion().equals(region)) score += 15;
        if (v.isAvailable()) score += 10;
        if (v.getExperience() != null) score += Math.min(v.getExperience() * 2, 20);
        int missionCount = v.getMissions() != null ? v.getMissions().size() : 0;
        if (missionCount < 3) score += 10;
        else if (missionCount < 5) score += 5;
        return score;
    }

    private Volunteer findById(Long id) {
        return volunteerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bénévole non trouvé avec l'id: " + id));
    }

    private VolunteerDTO toDTO(Volunteer v) {
        return VolunteerDTO.builder()
                .id(v.getId())
                .firstName(v.getFirstName())
                .lastName(v.getLastName())
                .email(v.getEmail())
                .phone(v.getPhone())
                .address(v.getAddress())
                .city(v.getCity())
                .region(v.getRegion())
                .skills(v.getSkills())
                .available(v.isAvailable())
                .avatar(v.getAvatar())
                .experience(v.getExperience())
                .userId(v.getUser() != null ? v.getUser().getId() : null)
                .missionCount(v.getMissions() != null ? v.getMissions().size() : 0)
                .createdAt(v.getCreatedAt())
                .updatedAt(v.getUpdatedAt())
                .build();
    }

    private Volunteer toEntity(VolunteerDTO dto) {
        return Volunteer.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .city(dto.getCity())
                .region(dto.getRegion())
                .skills(dto.getSkills())
                .available(dto.isAvailable())
                .experience(dto.getExperience())
                .build();
    }
}
