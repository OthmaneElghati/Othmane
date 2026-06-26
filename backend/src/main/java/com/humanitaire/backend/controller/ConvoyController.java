package com.humanitaire.backend.controller;

import com.humanitaire.backend.dto.ConvoyDTO;
import com.humanitaire.backend.service.ConvoyService;
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
@RequestMapping("/api/convoys")
@RequiredArgsConstructor
public class ConvoyController {

    private final ConvoyService convoyService;

    @GetMapping
    public ResponseEntity<Page<ConvoyDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDir.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy));
        if (search != null && !search.isEmpty()) return ResponseEntity.ok(convoyService.search(search, pageable));
        if (status != null && !status.isEmpty()) return ResponseEntity.ok(convoyService.getByStatus(status, pageable));
        return ResponseEntity.ok(convoyService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConvoyDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(convoyService.getById(id));
    }

    @GetMapping("/active")
    public ResponseEntity<List<ConvoyDTO>> getActive() {
        return ResponseEntity.ok(convoyService.getActiveConvoys());
    }

    @PostMapping
    public ResponseEntity<ConvoyDTO> create(@Valid @RequestBody ConvoyDTO dto) {
        return ResponseEntity.ok(convoyService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConvoyDTO> update(@PathVariable Long id, @Valid @RequestBody ConvoyDTO dto) {
        return ResponseEntity.ok(convoyService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        convoyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
