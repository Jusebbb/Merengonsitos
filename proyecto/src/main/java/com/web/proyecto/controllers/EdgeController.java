package com.web.proyecto.controllers;

import com.web.proyecto.dtos.EdgeDTO;
import com.web.proyecto.services.EdgeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/edges")
@RequiredArgsConstructor
@CrossOrigin
public class EdgeController {

    private final EdgeService service;

    @PostMapping
    public ResponseEntity<EdgeDTO> create(@RequestBody @Valid EdgeDTO dto) {
        EdgeDTO created = service.create(dto);
        return ResponseEntity
                .created(URI.create("/api/edges/" + created.getId()))
                .body(created);
    }

    // Usa service.list() y filtra por processId en el controller (sin tocar el service)
    @GetMapping
    public ResponseEntity<List<EdgeDTO>> list(@RequestParam(required = false) Long processId) {
        List<EdgeDTO> all = service.list();
        if (processId != null) {
            all = all.stream()
                    .filter(e -> Objects.equals(e.getProcessId(), processId))
                    .collect(Collectors.toList());
        }
        return ResponseEntity.ok(all);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EdgeDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EdgeDTO> update(@PathVariable Long id, @RequestBody @Valid EdgeDTO dto) {
        if (dto.getId() != null && !dto.getId().equals(id)) {
            throw new IllegalArgumentException("id path y dto.id no coinciden");
        }
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

