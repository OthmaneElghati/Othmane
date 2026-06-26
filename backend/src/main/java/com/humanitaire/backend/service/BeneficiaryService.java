package com.humanitaire.backend.service;

import com.humanitaire.backend.dto.BeneficiaryDTO;
import com.humanitaire.backend.entity.Beneficiary;
import com.humanitaire.backend.entity.Mission;
import com.humanitaire.backend.exception.ResourceNotFoundException;
import com.humanitaire.backend.repository.BeneficiaryRepository;
import com.humanitaire.backend.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;
    private final MissionRepository missionRepository;

    public Page<BeneficiaryDTO> getAll(Pageable pageable) {
        return beneficiaryRepository.findAll(pageable).map(this::toDTO);
    }

    public BeneficiaryDTO getById(Long id) {
        return toDTO(findById(id));
    }

    public Page<BeneficiaryDTO> search(String query, Pageable pageable) {
        return beneficiaryRepository.search(query, pageable).map(this::toDTO);
    }

    public Page<BeneficiaryDTO> getByEmergencyLevel(String level, Pageable pageable) {
        return beneficiaryRepository.findByEmergencyLevel(Beneficiary.EmergencyLevel.valueOf(level), pageable).map(this::toDTO);
    }

    public Page<BeneficiaryDTO> getByRegion(String region, Pageable pageable) {
        return beneficiaryRepository.findByRegion(region, pageable).map(this::toDTO);
    }

    public Page<BeneficiaryDTO> getByMission(Long missionId, Pageable pageable) {
        return beneficiaryRepository.findByMissionId(missionId, pageable).map(this::toDTO);
    }

    @Transactional
    public BeneficiaryDTO create(BeneficiaryDTO dto) {
        Beneficiary ben = toEntity(dto);
        ben.setEmergencyLevel(calculateEmergencyLevel(ben));
        return toDTO(beneficiaryRepository.save(ben));
    }

    @Transactional
    public BeneficiaryDTO update(Long id, BeneficiaryDTO dto) {
        Beneficiary ben = findById(id);
        ben.setFullName(dto.getFullName());
        ben.setFamilySize(dto.getFamilySize());
        if (dto.getEmergencyLevel() != null) {
            ben.setEmergencyLevel(Beneficiary.EmergencyLevel.valueOf(dto.getEmergencyLevel()));
        }
        ben.setAddress(dto.getAddress());
        ben.setCity(dto.getCity());
        ben.setRegion(dto.getRegion());
        ben.setPhone(dto.getPhone());
        ben.setNeeds(dto.getNeeds());
        if (dto.getMissionId() != null) {
            Mission mission = missionRepository.findById(dto.getMissionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Mission non trouvée"));
            ben.setMission(mission);
        }
        ben.setEmergencyLevel(calculateEmergencyLevel(ben));
        return toDTO(beneficiaryRepository.save(ben));
    }

    @Transactional
    public void delete(Long id) {
        beneficiaryRepository.deleteById(id);
    }

    public Beneficiary.EmergencyLevel calculateEmergencyLevel(Beneficiary ben) {
        int score = 0;
        if (ben.getFamilySize() != null) {
            if (ben.getFamilySize() > 8) score += 30;
            else if (ben.getFamilySize() > 5) score += 20;
            else if (ben.getFamilySize() > 3) score += 10;
        }
        if (ben.getNeeds() != null) {
            String needs = ben.getNeeds().toLowerCase();
            if (needs.contains("médic") || needs.contains("santé")) score += 25;
            if (needs.contains("alimentaire") || needs.contains("nourriture")) score += 20;
            if (needs.contains("logement") || needs.contains("abri")) score += 20;
            if (needs.contains("eau")) score += 15;
        }
        if (ben.getMission() == null) score += 10;
        if (score >= 60) return Beneficiary.EmergencyLevel.CRITICAL;
        if (score >= 40) return Beneficiary.EmergencyLevel.HIGH;
        if (score >= 20) return Beneficiary.EmergencyLevel.MEDIUM;
        return Beneficiary.EmergencyLevel.LOW;
    }

    private Beneficiary findById(Long id) {
        return beneficiaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bénéficiaire non trouvé avec l'id: " + id));
    }

    private BeneficiaryDTO toDTO(Beneficiary b) {
        return BeneficiaryDTO.builder()
                .id(b.getId())
                .fullName(b.getFullName())
                .familySize(b.getFamilySize())
                .emergencyLevel(b.getEmergencyLevel() != null ? b.getEmergencyLevel().name() : null)
                .address(b.getAddress())
                .city(b.getCity())
                .region(b.getRegion())
                .phone(b.getPhone())
                .needs(b.getNeeds())
                .missionId(b.getMission() != null ? b.getMission().getId() : null)
                .missionTitle(b.getMission() != null ? b.getMission().getTitle() : null)
                .createdAt(b.getCreatedAt())
                .updatedAt(b.getUpdatedAt())
                .build();
    }

    private Beneficiary toEntity(BeneficiaryDTO dto) {
        Beneficiary.BeneficiaryBuilder builder = Beneficiary.builder()
                .fullName(dto.getFullName())
                .familySize(dto.getFamilySize())
                .address(dto.getAddress())
                .city(dto.getCity())
                .region(dto.getRegion())
                .phone(dto.getPhone())
                .needs(dto.getNeeds());
        if (dto.getEmergencyLevel() != null) {
            builder.emergencyLevel(Beneficiary.EmergencyLevel.valueOf(dto.getEmergencyLevel()));
        }
        return builder.build();
    }
}
