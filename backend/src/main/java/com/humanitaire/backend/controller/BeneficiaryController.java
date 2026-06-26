package com.humanitaire.backend.controller;

import com.humanitaire.backend.dto.BeneficiaryDTO;
import com.humanitaire.backend.service.BeneficiaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/beneficiaries")
@RequiredArgsConstructor
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    @GetMapping
    public ResponseEntity<Page<BeneficiaryDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String emergencyLevel,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) Long missionId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDir.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy));
        if (search != null && !search.isEmpty()) return ResponseEntity.ok(beneficiaryService.search(search, pageable));
        if (emergencyLevel != null && !emergencyLevel.isEmpty()) return ResponseEntity.ok(beneficiaryService.getByEmergencyLevel(emergencyLevel, pageable));
        if (region != null && !region.isEmpty()) return ResponseEntity.ok(beneficiaryService.getByRegion(region, pageable));
        if (missionId != null) return ResponseEntity.ok(beneficiaryService.getByMission(missionId, pageable));
        return ResponseEntity.ok(beneficiaryService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeneficiaryDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(beneficiaryService.getById(id));
    }

    @PostMapping
    public ResponseEntity<BeneficiaryDTO> create(@Valid @RequestBody BeneficiaryDTO dto) {
        return ResponseEntity.ok(beneficiaryService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BeneficiaryDTO> update(@PathVariable Long id, @Valid @RequestBody BeneficiaryDTO dto) {
        return ResponseEntity.ok(beneficiaryService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        beneficiaryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
