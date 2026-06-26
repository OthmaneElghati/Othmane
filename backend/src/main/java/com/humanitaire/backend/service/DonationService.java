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

import java.util.*;

@Service
@RequiredArgsConstructor
public class DonationService {

    private final DonationRepository donationRepository;
    private final MissionRepository missionRepository;
    private final UserRepository userRepository;

    public Page<DonationDTO> getAll(Pageable pageable) {
        return donationRepository.findAll(pageable).map(this::toDTO);
    }

    public DonationDTO getById(Long id) {
        return toDTO(findById(id));
    }

    public Page<DonationDTO> search(String query, Pageable pageable) {
        return donationRepository.search(query, pageable).map(this::toDTO);
    }

    public Page<DonationDTO> getByDonor(Long donorId, Pageable pageable) {
        return donationRepository.findByDonorId(donorId, pageable).map(this::toDTO);
    }

    public Page<DonationDTO> getByStatus(String status, Pageable pageable) {
        return donationRepository.findByStatus(Donation.DonationStatus.valueOf(status), pageable).map(this::toDTO);
    }

    @Transactional
    public DonationDTO create(DonationDTO dto) {
        Donation donation = toEntity(dto);
        donation.setTransactionId(UUID.randomUUID().toString().substring(0, 12).toUpperCase());
        return toDTO(donationRepository.save(donation));
    }

    @Transactional
    public DonationDTO update(Long id, DonationDTO dto) {
        Donation donation = findById(id);
        donation.setDonorName(dto.getDonorName());
        donation.setDonorEmail(dto.getDonorEmail());
        donation.setAmount(dto.getAmount());
        donation.setCurrency(dto.getCurrency() != null ? dto.getCurrency() : "MAD");
        if (dto.getPaymentMethod() != null) {
            donation.setPaymentMethod(Donation.PaymentMethod.valueOf(dto.getPaymentMethod()));
        }
        if (dto.getStatus() != null) {
            donation.setStatus(Donation.DonationStatus.valueOf(dto.getStatus()));
        }
        donation.setDescription(dto.getDescription());
        if (dto.getMissionId() != null) {
            Mission mission = missionRepository.findById(dto.getMissionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Mission non trouvée"));
            donation.setMission(mission);
        }
        return toDTO(donationRepository.save(donation));
    }

    @Transactional
    public void delete(Long id) {
        donationRepository.deleteById(id);
    }

    public Map<String, Object> getDonorStats(Long donorId) {
        Map<String, Object> stats = new HashMap<>();
        long donationCount = donationRepository.countByDonorId(donorId);
        Double totalAmount = donationRepository.sumByDonorId(donorId);
        stats.put("totalDonations", donationCount);
        stats.put("totalAmount", totalAmount != null ? totalAmount : 0.0);
        stats.put("loyaltyCategory", calculateLoyaltyCategory(donationCount, totalAmount));
        stats.put("loyaltyScore", calculateLoyaltyScore(donationCount, totalAmount));
        return stats;
    }

    public String calculateLoyaltyCategory(long donationCount, Double totalAmount) {
        double amount = totalAmount != null ? totalAmount : 0.0;
        if (donationCount >= 10 && amount >= 50000) return "LOYAL";
        if (donationCount >= 5 && amount >= 20000) return "REGULAR";
        if (donationCount >= 2) return "OCCASIONAL";
        return "NEW";
    }

    public int calculateLoyaltyScore(long donationCount, Double totalAmount) {
        int score = 0;
        score += (int) Math.min(donationCount * 10, 40);
        double amount = totalAmount != null ? totalAmount : 0.0;
        if (amount >= 100000) score += 30;
        else if (amount >= 50000) score += 25;
        else if (amount >= 20000) score += 20;
        else if (amount >= 5000) score += 10;
        else score += 5;
        return Math.min(score, 100);
    }

    private Donation findById(Long id) {
        return donationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Donation non trouvée avec l'id: " + id));
    }

    private DonationDTO toDTO(Donation d) {
        return DonationDTO.builder()
                .id(d.getId())
                .donorName(d.getDonorName())
                .donorEmail(d.getDonorEmail())
                .amount(d.getAmount())
                .currency(d.getCurrency())
                .paymentMethod(d.getPaymentMethod() != null ? d.getPaymentMethod().name() : null)
                .status(d.getStatus() != null ? d.getStatus().name() : null)
                .transactionId(d.getTransactionId())
                .description(d.getDescription())
                .missionId(d.getMission() != null ? d.getMission().getId() : null)
                .missionTitle(d.getMission() != null ? d.getMission().getTitle() : null)
                .donorId(d.getDonor() != null ? d.getDonor().getId() : null)
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .build();
    }

    private Donation toEntity(DonationDTO dto) {
        Donation.DonationBuilder builder = Donation.builder()
                .donorName(dto.getDonorName())
                .donorEmail(dto.getDonorEmail())
                .amount(dto.getAmount())
                .currency(dto.getCurrency() != null ? dto.getCurrency() : "MAD")
                .description(dto.getDescription())
                .status(dto.getStatus() != null ? Donation.DonationStatus.valueOf(dto.getStatus()) : Donation.DonationStatus.PENDING);
        if (dto.getPaymentMethod() != null) {
            builder.paymentMethod(Donation.PaymentMethod.valueOf(dto.getPaymentMethod()));
        }
        if (dto.getMissionId() != null) {
            missionRepository.findById(dto.getMissionId()).ifPresent(builder::mission);
        }
        if (dto.getDonorId() != null) {
            userRepository.findById(dto.getDonorId()).ifPresent(builder::donor);
        }
        return builder.build();
    }
}
