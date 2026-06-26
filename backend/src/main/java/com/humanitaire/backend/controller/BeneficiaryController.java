package com.humanitaire.backend.controller;

import com.humanitaire.backend.dto.BeneficiaryDTO;
import com.humanitaire.backend.service.BeneficiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/beneficiaries")
@RequiredArgsConstructor
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    @GetMapping
    public ResponseEntity<Page<BeneficiaryDTO>> getAllBeneficiaries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return ResponseEntity.ok(beneficiaryService.getAllBeneficiaries(PageRequest.of(page, size, sort)));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<BeneficiaryDTO>> searchBeneficiaries(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(beneficiaryService.searchBeneficiaries(q, PageRequest.of(page, size)));
    }

    @GetMapping("/mission/{missionId}")
    public ResponseEntity<Page<BeneficiaryDTO>> getBeneficiariesByMission(
            @PathVariable Long missionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(beneficiaryService.getBeneficiariesByMission(missionId, PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeneficiaryDTO> getBeneficiaryById(@PathVariable Long id) {
        return ResponseEntity.ok(beneficiaryService.getBeneficiaryById(id));
    }

    @PostMapping
    public ResponseEntity<BeneficiaryDTO> createBeneficiary(@RequestBody BeneficiaryDTO dto) {
        return ResponseEntity.ok(beneficiaryService.createBeneficiary(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BeneficiaryDTO> updateBeneficiary(@PathVariable Long id, @RequestBody BeneficiaryDTO dto) {
        return ResponseEntity.ok(beneficiaryService.updateBeneficiary(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBeneficiary(@PathVariable Long id) {
        beneficiaryService.deleteBeneficiary(id);
        return ResponseEntity.noContent().build();
    }
}
