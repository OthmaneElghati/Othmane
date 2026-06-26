package com.humanitaire.backend.controller;

import com.humanitaire.backend.dto.MissionDTO;
import com.humanitaire.backend.service.MissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;

    @GetMapping
    public ResponseEntity<Page<MissionDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String priority) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDir.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy));
        if (search != null && !search.isEmpty()) return ResponseEntity.ok(missionService.search(search, pageable));
        if (status != null && !status.isEmpty()) return ResponseEntity.ok(missionService.getByStatus(status, pageable));
        if (region != null && !region.isEmpty()) return ResponseEntity.ok(missionService.getByRegion(region, pageable));
        if (priority != null && !priority.isEmpty()) return ResponseEntity.ok(missionService.getByPriority(priority, pageable));
        return ResponseEntity.ok(missionService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MissionDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(missionService.getById(id));
    }

    @GetMapping("/recommended")
    public ResponseEntity<List<MissionDTO>> getRecommended() {
        return ResponseEntity.ok(missionService.getRecommendedMissions());
    }

    @GetMapping("/manager/{managerId}")
    public ResponseEntity<Page<MissionDTO>> getByManager(@PathVariable Long managerId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(missionService.getByManager(managerId, PageRequest.of(page, size)));
    }

    @PostMapping
    public ResponseEntity<MissionDTO> create(@Valid @RequestBody MissionDTO dto) {
        return ResponseEntity.ok(missionService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MissionDTO> update(@PathVariable Long id, @Valid @RequestBody MissionDTO dto) {
        return ResponseEntity.ok(missionService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        missionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{missionId}/volunteers/{volunteerId}")
    public ResponseEntity<MissionDTO> assignVolunteer(@PathVariable Long missionId, @PathVariable Long volunteerId) {
        return ResponseEntity.ok(missionService.assignVolunteer(missionId, volunteerId));
    }

    @DeleteMapping("/{missionId}/volunteers/{volunteerId}")
    public ResponseEntity<MissionDTO> removeVolunteer(@PathVariable Long missionId, @PathVariable Long volunteerId) {
        return ResponseEntity.ok(missionService.removeVolunteer(missionId, volunteerId));
    }
}
