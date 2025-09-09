package com.web.proyecto.controllers;

import com.web.proyecto.dtos.ProcessDTO;
import com.web.proyecto.entities.Process;
import com.web.proyecto.services.ProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/processes")
@RequiredArgsConstructor
@CrossOrigin
public class ProcessController {

    private final ProcessService service;

    @PostMapping
    public ResponseEntity<ProcessDTO> create(@RequestBody ProcessDTO dto) {
        ProcessDTO created = service.create(dto);
        return ResponseEntity.created(URI.create("/api/processes/" + created.getId())).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Process>> list() {
        return ResponseEntity.ok(service.list());
    }

    @GetMapping("/{empresaId}")
    public ResponseEntity<Object> getByEmpresaId(@PathVariable Long empresaId) {
        return ResponseEntity.ok(service.getByEmpresaId(empresaId));
    }

    @PutMapping("/{empresaId}")
    public ResponseEntity<Object> updateByEmpresaId(@PathVariable Long empresaId,
                                                    @RequestBody ProcessDTO dto) {
        return ResponseEntity.ok(service.updateByEmpresaId(empresaId, dto));
    }

    @DeleteMapping("/{empresaId}")
    public ResponseEntity<Void> deleteByEmpresaId(@PathVariable Long empresaId) {
        service.deleteByEmpresaId(empresaId);
        return ResponseEntity.noContent().build();
    }
}
