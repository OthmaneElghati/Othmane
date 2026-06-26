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

    public Page<BeneficiaryDTO> getAllBeneficiaries(Pageable pageable) {
        return beneficiaryRepository.findAll(pageable).map(this::toDTO);
    }

    public Page<BeneficiaryDTO> searchBeneficiaries(String query, Pageable pageable) {
        return beneficiaryRepository.findByFullNameContainingIgnoreCase(query, pageable).map(this::toDTO);
    }

    public Page<BeneficiaryDTO> getBeneficiariesByMission(Long missionId, Pageable pageable) {
        return beneficiaryRepository.findByMissionId(missionId, pageable).map(this::toDTO);
    }

    public BeneficiaryDTO getBeneficiaryById(Long id) {
        Beneficiary beneficiary = beneficiaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bénéficiaire", "id", id));
        return toDTO(beneficiary);
    }

    @Transactional
    public BeneficiaryDTO createBeneficiary(BeneficiaryDTO dto) {
        Beneficiary beneficiary = toEntity(dto);
        if (dto.getMissionId() != null) {
            Mission mission = missionRepository.findById(dto.getMissionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Mission", "id", dto.getMissionId()));
            beneficiary.setMission(mission);
        }
        beneficiary = beneficiaryRepository.save(beneficiary);
        return toDTO(beneficiary);
    }

    @Transactional
    public BeneficiaryDTO updateBeneficiary(Long id, BeneficiaryDTO dto) {
        Beneficiary beneficiary = beneficiaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bénéficiaire", "id", id));

        beneficiary.setFullName(dto.getFullName());
        beneficiary.setFamilySize(dto.getFamilySize());
        beneficiary.setEmergencyLevel(Beneficiary.EmergencyLevel.valueOf(dto.getEmergencyLevel()));
        beneficiary.setAddress(dto.getAddress());
        beneficiary.setCity(dto.getCity());
        beneficiary.setRegion(dto.getRegion());
        beneficiary.setPhone(dto.getPhone());
        beneficiary.setNeeds(dto.getNeeds());

        if (dto.getMissionId() != null) {
            Mission mission = missionRepository.findById(dto.getMissionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Mission", "id", dto.getMissionId()));
            beneficiary.setMission(mission);
        }

        beneficiary = beneficiaryRepository.save(beneficiary);
        return toDTO(beneficiary);
    }

    public void deleteBeneficiary(Long id) {
        if (!beneficiaryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Bénéficiaire", "id", id);
        }
        beneficiaryRepository.deleteById(id);
    }

    private BeneficiaryDTO toDTO(Beneficiary beneficiary) {
        return BeneficiaryDTO.builder()
                .id(beneficiary.getId())
                .fullName(beneficiary.getFullName())
                .familySize(beneficiary.getFamilySize())
                .emergencyLevel(beneficiary.getEmergencyLevel() != null ? beneficiary.getEmergencyLevel().name() : null)
                .address(beneficiary.getAddress())
                .city(beneficiary.getCity())
                .region(beneficiary.getRegion())
                .phone(beneficiary.getPhone())
                .needs(beneficiary.getNeeds())
                .missionId(beneficiary.getMission() != null ? beneficiary.getMission().getId() : null)
                .missionTitle(beneficiary.getMission() != null ? beneficiary.getMission().getTitle() : null)
                .build();
    }

    private Beneficiary toEntity(BeneficiaryDTO dto) {
        return Beneficiary.builder()
                .fullName(dto.getFullName())
                .familySize(dto.getFamilySize())
                .emergencyLevel(dto.getEmergencyLevel() != null ? Beneficiary.EmergencyLevel.valueOf(dto.getEmergencyLevel()) : Beneficiary.EmergencyLevel.MEDIUM)
                .address(dto.getAddress())
                .city(dto.getCity())
                .region(dto.getRegion())
                .phone(dto.getPhone())
                .needs(dto.getNeeds())
                .build();
    }
}
