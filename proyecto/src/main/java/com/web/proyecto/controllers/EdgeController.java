package com.web.proyecto.controllers;

import com.web.proyecto.dtos.EdgeDTO;
import com.web.proyecto.services.EdgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/edges")
@RequiredArgsConstructor
@CrossOrigin
public class EdgeController {

    private final EdgeService service;

    @GetMapping
    public ResponseEntity<List<EdgeDTO>> list(@RequestParam(required = false) Long processId) {
        return ResponseEntity.ok(processId != null ? service.listByProcess(processId) : service.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.getById(id));
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        }
    }

    @PostMapping // sin "consumes" para evitar 415 por charset
    public ResponseEntity<?> create(@RequestBody EdgeDTO dto) {
        try {
            EdgeDTO created = service.create(dto);
            return ResponseEntity.created(URI.create("/api/edges/" + created.getId())).body(created);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage()); // 400
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());  // 404
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody EdgeDTO dto) {
        try {
            return ResponseEntity.ok(service.update(id, dto));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        }
    }
}
