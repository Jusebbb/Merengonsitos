package com.web.proyecto.controllers;

import com.web.proyecto.dtos.EmpresaDTO;
import com.web.proyecto.services.EmpresaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/empresas")
@RequiredArgsConstructor
@CrossOrigin
@Validated
public class EmpresaController {

    private final EmpresaService empresaService;

    // CREATE (HU-01) -> crea empresa y admin inicial; devuelve 201 + Location
    @PostMapping
    public ResponseEntity<EmpresaDTO> create(@RequestBody @Valid EmpresaDTO dto) {
        EmpresaDTO created = empresaService.create(dto);
        return ResponseEntity
                .created(URI.create("/api/empresas/" + created.getId()))
                .body(created);
    }

    // LIST (solo activas, por soft-delete en service)
    @GetMapping
    public ResponseEntity<List<EmpresaDTO>> list() {
        return ResponseEntity.ok(empresaService.list());
    }

    // GET BY ID (solo activa; si está inactiva, el service lanza error)
    @GetMapping("/{id}")
    public ResponseEntity<EmpresaDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(empresaService.getById(id));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<EmpresaDTO> update(@PathVariable Long id, @RequestBody @Valid EmpresaDTO dto) {
        return ResponseEntity.ok(empresaService.update(id, dto));
    }

    // SOFT-DELETE (marca active=false)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        empresaService.delete(id);  // soft-delete en el service
        return ResponseEntity.noContent().build();
    }
}
