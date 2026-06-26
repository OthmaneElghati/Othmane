package com.humanitaire.backend.controller;

import com.humanitaire.backend.dto.MissionDTO;
import com.humanitaire.backend.service.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;

    @GetMapping
    public ResponseEntity<Page<MissionDTO>> getAllMissions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(missionService.getAllMissions(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<MissionDTO>> searchMissions(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(missionService.searchMissions(q, PageRequest.of(page, size)));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<MissionDTO>> getMissionsByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(missionService.getMissionsByStatus(status, PageRequest.of(page, size)));
    }

    @GetMapping("/region/{region}")
    public ResponseEntity<Page<MissionDTO>> getMissionsByRegion(
            @PathVariable String region,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(missionService.getMissionsByRegion(region, PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MissionDTO> getMissionById(@PathVariable Long id) {
        return ResponseEntity.ok(missionService.getMissionById(id));
    }

    @PostMapping
    public ResponseEntity<MissionDTO> createMission(@RequestBody MissionDTO dto) {
        return ResponseEntity.ok(missionService.createMission(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MissionDTO> updateMission(@PathVariable Long id, @RequestBody MissionDTO dto) {
        return ResponseEntity.ok(missionService.updateMission(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMission(@PathVariable Long id) {
        missionService.deleteMission(id);
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
