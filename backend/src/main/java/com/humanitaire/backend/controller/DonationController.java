package com.humanitaire.backend.controller;

import com.humanitaire.backend.dto.DonationDTO;
import com.humanitaire.backend.service.DonationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/donations")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;

    @GetMapping
    public ResponseEntity<Page<DonationDTO>> getAllDonations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        size = Math.min(size, 100);
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return ResponseEntity.ok(donationService.getAllDonations(PageRequest.of(page, size, sort)));
    }

    @GetMapping("/donor/{donorId}")
    public ResponseEntity<Page<DonationDTO>> getDonationsByDonor(
            @PathVariable Long donorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        size = Math.min(size, 100);
        return ResponseEntity.ok(donationService.getDonationsByDonor(donorId, PageRequest.of(page, size)));
    }

    @GetMapping("/mission/{missionId}")
    public ResponseEntity<Page<DonationDTO>> getDonationsByMission(
            @PathVariable Long missionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        size = Math.min(size, 100);
        return ResponseEntity.ok(donationService.getDonationsByMission(missionId, PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DonationDTO> getDonationById(@PathVariable Long id) {
        return ResponseEntity.ok(donationService.getDonationById(id));
    }

    @PostMapping
    public ResponseEntity<DonationDTO> createDonation(@RequestBody DonationDTO dto) {
        return ResponseEntity.ok(donationService.createDonation(dto));
    }

    @GetMapping("/total")
    public ResponseEntity<Map<String, Double>> getTotalAmount() {
        return ResponseEntity.ok(Map.of("total", donationService.getTotalDonationAmount()));
    }
}
