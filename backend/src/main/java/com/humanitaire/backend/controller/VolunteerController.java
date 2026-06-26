package com.humanitaire.backend.controller;

import com.humanitaire.backend.dto.VolunteerDTO;
import com.humanitaire.backend.service.VolunteerService;
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
@RequestMapping("/api/volunteers")
@RequiredArgsConstructor
public class VolunteerController {

    private final VolunteerService volunteerService;

    @GetMapping
    public ResponseEntity<Page<VolunteerDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) Boolean available) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDir.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy));
        if (search != null && !search.isEmpty()) return ResponseEntity.ok(volunteerService.search(search, pageable));
        if (region != null && !region.isEmpty()) return ResponseEntity.ok(volunteerService.getByRegion(region, pageable));
        if (available != null && available) return ResponseEntity.ok(volunteerService.getAvailable(pageable));
        return ResponseEntity.ok(volunteerService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VolunteerDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(volunteerService.getById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<VolunteerDTO> getByUserId(@PathVariable Long userId) {
        VolunteerDTO dto = volunteerService.getByUserId(userId);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @GetMapping("/recommend")
    public ResponseEntity<List<VolunteerDTO>> recommend(
            @RequestParam String region,
            @RequestParam(required = false) String skills) {
        return ResponseEntity.ok(volunteerService.recommendForMission(region, skills));
    }

    @PostMapping
    public ResponseEntity<VolunteerDTO> create(@Valid @RequestBody VolunteerDTO dto) {
        return ResponseEntity.ok(volunteerService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VolunteerDTO> update(@PathVariable Long id, @Valid @RequestBody VolunteerDTO dto) {
        return ResponseEntity.ok(volunteerService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        volunteerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
