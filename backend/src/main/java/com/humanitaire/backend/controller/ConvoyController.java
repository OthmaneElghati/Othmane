package com.humanitaire.backend.controller;

import com.humanitaire.backend.dto.ConvoyDTO;
import com.humanitaire.backend.service.ConvoyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    public ResponseEntity<Page<ConvoyDTO>> getAllConvoys(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return ResponseEntity.ok(convoyService.getAllConvoys(PageRequest.of(page, size, sort)));
    }

    @GetMapping("/active")
    public ResponseEntity<List<ConvoyDTO>> getActiveConvoys() {
        return ResponseEntity.ok(convoyService.getActiveConvoys());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConvoyDTO> getConvoyById(@PathVariable Long id) {
        return ResponseEntity.ok(convoyService.getConvoyById(id));
    }

    @PostMapping
    public ResponseEntity<ConvoyDTO> createConvoy(@RequestBody ConvoyDTO dto) {
        return ResponseEntity.ok(convoyService.createConvoy(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConvoyDTO> updateConvoy(@PathVariable Long id, @RequestBody ConvoyDTO dto) {
        return ResponseEntity.ok(convoyService.updateConvoy(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConvoy(@PathVariable Long id) {
        convoyService.deleteConvoy(id);
        return ResponseEntity.noContent().build();
    }
}
