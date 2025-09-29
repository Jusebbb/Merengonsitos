package com.web.proyecto.controllers;

import com.web.proyecto.dtos.RolDTO;
import com.web.proyecto.services.RolService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/empresas/{empresaId}/roles")
@RequiredArgsConstructor
@Validated
@CrossOrigin
public class RolController {

    private final RolService rolService;

    // HU-17: crear rol en una empresa
    @PostMapping
    public ResponseEntity<RolDTO> create(@PathVariable Long empresaId, @RequestBody @Valid RolDTO dto) {
        RolDTO created = rolService.create(empresaId, dto);
        return ResponseEntity.created(URI.create(
                "/api/empresas/" + empresaId + "/roles/" + created.getId()))
                .body(created);
    }

    // HU-20: listar roles con usageCount
    @GetMapping
    public ResponseEntity<List<RolDTO>> list(@PathVariable Long empresaId) {
        return ResponseEntity.ok(rolService.list(empresaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolDTO> getById(@PathVariable Long empresaId, @PathVariable Long id) {
        return ResponseEntity.ok(rolService.getById(empresaId, id));
    }

    // HU-18: editar rol
    @PutMapping("/{id}")
    public ResponseEntity<RolDTO> update(@PathVariable Long empresaId,
                                        @PathVariable Long id,
                                        @RequestBody @Valid RolDTO dto) {
        return ResponseEntity.ok(rolService.update(empresaId, id, dto));
    }

    // HU-19: eliminar rol si no está en uso
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long empresaId,
                                        @PathVariable Long id) {
        rolService.delete(empresaId, id);
        return ResponseEntity.noContent().build();
    }
}