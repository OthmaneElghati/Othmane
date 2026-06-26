package com.humanitaire.backend.service;

import com.humanitaire.backend.dto.DonationDTO;
import com.humanitaire.backend.entity.Donation;
import com.humanitaire.backend.entity.Mission;
import com.humanitaire.backend.entity.User;
import com.humanitaire.backend.exception.ResourceNotFoundException;
import com.humanitaire.backend.repository.DonationRepository;
import com.humanitaire.backend.repository.MissionRepository;
import com.humanitaire.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DonationService {

    private final DonationRepository donationRepository;
    private final MissionRepository missionRepository;
    private final UserRepository userRepository;

    public Page<DonationDTO> getAllDonations(Pageable pageable) {
        return donationRepository.findAll(pageable).map(this::toDTO);
    }

    public Page<DonationDTO> getDonationsByDonor(Long donorId, Pageable pageable) {
        return donationRepository.findByDonorId(donorId, pageable).map(this::toDTO);
    }

    public Page<DonationDTO> getDonationsByMission(Long missionId, Pageable pageable) {
        return donationRepository.findByMissionId(missionId, pageable).map(this::toDTO);
    }

    public DonationDTO getDonationById(Long id) {
        Donation donation = donationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Donation", "id", id));
        return toDTO(donation);
    }

    @Transactional
    public DonationDTO createDonation(DonationDTO dto) {
        Donation donation = Donation.builder()
                .donorName(dto.getDonorName())
                .donorEmail(dto.getDonorEmail())
                .amount(dto.getAmount())
                .currency(dto.getCurrency() != null ? dto.getCurrency() : "MAD")
                .paymentMethod(dto.getPaymentMethod() != null ? Donation.PaymentMethod.valueOf(dto.getPaymentMethod()) : Donation.PaymentMethod.CMI)
                .status(Donation.DonationStatus.COMPLETED)
                .transactionId(UUID.randomUUID().toString().substring(0, 12).toUpperCase())
                .description(dto.getDescription())
                .build();

        if (dto.getMissionId() != null) {
            Mission mission = missionRepository.findById(dto.getMissionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Mission", "id", dto.getMissionId()));
            donation.setMission(mission);
        }

        donation = donationRepository.save(donation);
        return toDTO(donation);
    }

    public Double getTotalDonationAmount() {
        Double total = donationRepository.getTotalDonationAmount();
        return total != null ? total : 0.0;
    }

    private DonationDTO toDTO(Donation donation) {
        return DonationDTO.builder()
                .id(donation.getId())
                .donorName(donation.getDonorName())
                .donorEmail(donation.getDonorEmail())
                .amount(donation.getAmount())
                .currency(donation.getCurrency())
                .paymentMethod(donation.getPaymentMethod() != null ? donation.getPaymentMethod().name() : null)
                .status(donation.getStatus() != null ? donation.getStatus().name() : null)
                .transactionId(donation.getTransactionId())
                .description(donation.getDescription())
                .missionId(donation.getMission() != null ? donation.getMission().getId() : null)
                .missionTitle(donation.getMission() != null ? donation.getMission().getTitle() : null)
                .createdAt(donation.getCreatedAt())
                .build();
    }
}
