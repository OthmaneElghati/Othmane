package com.humanitaire.backend.controller;

import com.humanitaire.backend.dto.DonationDTO;
import com.humanitaire.backend.service.DonationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<Page<DonationDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long donorId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDir.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy));
        if (search != null && !search.isEmpty()) return ResponseEntity.ok(donationService.search(search, pageable));
        if (status != null && !status.isEmpty()) return ResponseEntity.ok(donationService.getByStatus(status, pageable));
        if (donorId != null) return ResponseEntity.ok(donationService.getByDonor(donorId, pageable));
        return ResponseEntity.ok(donationService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DonationDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(donationService.getById(id));
    }

    @GetMapping("/donor/{donorId}/stats")
    public ResponseEntity<Map<String, Object>> getDonorStats(@PathVariable Long donorId) {
        return ResponseEntity.ok(donationService.getDonorStats(donorId));
    }

    @PostMapping
    public ResponseEntity<DonationDTO> create(@Valid @RequestBody DonationDTO dto) {
        return ResponseEntity.ok(donationService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DonationDTO> update(@PathVariable Long id, @Valid @RequestBody DonationDTO dto) {
        return ResponseEntity.ok(donationService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        donationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
