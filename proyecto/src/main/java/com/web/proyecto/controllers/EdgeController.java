package com.web.proyecto.controllers;

import com.web.proyecto.dtos.EdgeDTO;
import com.web.proyecto.services.EdgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/edges")
@RequiredArgsConstructor
@CrossOrigin
public class EdgeController {

    private final EdgeService service;

    @PostMapping
    public ResponseEntity<EdgeDTO> create(@RequestBody EdgeDTO dto) {
        EdgeDTO created = service.create(dto);
        return ResponseEntity.created(URI.create("/api/edges/" + created.getId())).body(created);
    }

    @GetMapping
    public ResponseEntity<List<EdgeDTO>> list() {
        return ResponseEntity.ok(service.list());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EdgeDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EdgeDTO> update(@PathVariable Long id, @RequestBody EdgeDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

