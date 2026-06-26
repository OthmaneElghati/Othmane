package com.humanitaire.backend.controller;

import com.humanitaire.backend.dto.VolunteerDTO;
import com.humanitaire.backend.service.VolunteerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/volunteers")
@RequiredArgsConstructor
public class VolunteerController {

    private final VolunteerService volunteerService;

    @GetMapping
    public ResponseEntity<Page<VolunteerDTO>> getAllVolunteers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return ResponseEntity.ok(volunteerService.getAllVolunteers(PageRequest.of(page, size, sort)));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<VolunteerDTO>> searchVolunteers(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(volunteerService.searchVolunteers(q, PageRequest.of(page, size)));
    }

    @GetMapping("/available")
    public ResponseEntity<Page<VolunteerDTO>> getAvailableVolunteers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(volunteerService.getAvailableVolunteers(PageRequest.of(page, size)));
    }

    @GetMapping("/recommend")
    public ResponseEntity<List<VolunteerDTO>> recommendVolunteers(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String skill) {
        return ResponseEntity.ok(volunteerService.recommendVolunteers(region, skill));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VolunteerDTO> getVolunteerById(@PathVariable Long id) {
        return ResponseEntity.ok(volunteerService.getVolunteerById(id));
    }

    @PostMapping
    public ResponseEntity<VolunteerDTO> createVolunteer(@RequestBody VolunteerDTO dto) {
        return ResponseEntity.ok(volunteerService.createVolunteer(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VolunteerDTO> updateVolunteer(@PathVariable Long id, @RequestBody VolunteerDTO dto) {
        return ResponseEntity.ok(volunteerService.updateVolunteer(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVolunteer(@PathVariable Long id) {
        volunteerService.deleteVolunteer(id);
        return ResponseEntity.noContent().build();
    }
}
