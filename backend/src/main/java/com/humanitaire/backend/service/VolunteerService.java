package com.humanitaire.backend.service;

import com.humanitaire.backend.dto.VolunteerDTO;
import com.humanitaire.backend.entity.Volunteer;
import com.humanitaire.backend.exception.ResourceNotFoundException;
import com.humanitaire.backend.repository.VolunteerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VolunteerService {

    private final VolunteerRepository volunteerRepository;

    public Page<VolunteerDTO> getAllVolunteers(Pageable pageable) {
        return volunteerRepository.findAll(pageable).map(this::toDTO);
    }

    public Page<VolunteerDTO> searchVolunteers(String query, Pageable pageable) {
        return volunteerRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(query, query, pageable).map(this::toDTO);
    }

    public Page<VolunteerDTO> getAvailableVolunteers(Pageable pageable) {
        return volunteerRepository.findByAvailable(true, pageable).map(this::toDTO);
    }

    public VolunteerDTO getVolunteerById(Long id) {
        Volunteer volunteer = volunteerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bénévole", "id", id));
        return toDTO(volunteer);
    }

    @Transactional
    public VolunteerDTO createVolunteer(VolunteerDTO dto) {
        Volunteer volunteer = toEntity(dto);
        volunteer = volunteerRepository.save(volunteer);
        return toDTO(volunteer);
    }

    @Transactional
    public VolunteerDTO updateVolunteer(Long id, VolunteerDTO dto) {
        Volunteer volunteer = volunteerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bénévole", "id", id));

        volunteer.setFirstName(dto.getFirstName());
        volunteer.setLastName(dto.getLastName());
        volunteer.setEmail(dto.getEmail());
        volunteer.setPhone(dto.getPhone());
        volunteer.setAddress(dto.getAddress());
        volunteer.setCity(dto.getCity());
        volunteer.setRegion(dto.getRegion());
        volunteer.setSkills(dto.getSkills());
        volunteer.setAvailable(dto.isAvailable());

        volunteer = volunteerRepository.save(volunteer);
        return toDTO(volunteer);
    }

    public void deleteVolunteer(Long id) {
        if (!volunteerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Bénévole", "id", id);
        }
        volunteerRepository.deleteById(id);
    }

    public List<VolunteerDTO> recommendVolunteers(String region, String skill) {
        List<Volunteer> volunteers;
        if (skill != null && !skill.isEmpty()) {
            volunteers = volunteerRepository.findBySkillContaining(skill);
        } else if (region != null && !region.isEmpty()) {
            volunteers = volunteerRepository.findAvailableByRegion(region);
        } else {
            volunteers = volunteerRepository.findByAvailable(true, Pageable.unpaged()).getContent();
        }
        return volunteers.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private VolunteerDTO toDTO(Volunteer volunteer) {
        return VolunteerDTO.builder()
                .id(volunteer.getId())
                .firstName(volunteer.getFirstName())
                .lastName(volunteer.getLastName())
                .email(volunteer.getEmail())
                .phone(volunteer.getPhone())
                .address(volunteer.getAddress())
                .city(volunteer.getCity())
                .region(volunteer.getRegion())
                .skills(volunteer.getSkills())
                .available(volunteer.isAvailable())
                .avatar(volunteer.getAvatar())
                .missionCount(volunteer.getMissions() != null ? volunteer.getMissions().size() : 0)
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
                .build();
    }
}
