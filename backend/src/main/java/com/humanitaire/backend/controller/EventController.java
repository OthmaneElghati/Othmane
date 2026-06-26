package com.humanitaire.backend.controller;

import com.humanitaire.backend.dto.EventDTO;
import com.humanitaire.backend.service.EventService;
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
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<Page<EventDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "eventDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDir.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy));
        if (search != null && !search.isEmpty()) return ResponseEntity.ok(eventService.search(search, pageable));
        if (status != null && !status.isEmpty()) return ResponseEntity.ok(eventService.getByStatus(status, pageable));
        return ResponseEntity.ok(eventService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getById(id));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<EventDTO>> getUpcoming() {
        return ResponseEntity.ok(eventService.getUpcomingEvents());
    }

    @PostMapping
    public ResponseEntity<EventDTO> create(@Valid @RequestBody EventDTO dto) {
        return ResponseEntity.ok(eventService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventDTO> update(@PathVariable Long id, @Valid @RequestBody EventDTO dto) {
        return ResponseEntity.ok(eventService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
