package com.web.proyecto.controllers;

import com.web.proyecto.dtos.ActivityDTO;
import com.web.proyecto.services.ActivityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/activities")
public class ActivityController {

    private final ActivityService activityService;

    // ===== HU-08: crear =====
    @PostMapping
    public ResponseEntity<ActivityDTO> create(@Valid @RequestBody ActivityDTO dto) {
        return ResponseEntity.ok(activityService.create(dto));
    }

    // ===== HU-09: editar (con historial) =====
    @PutMapping("/{id}")
    public ResponseEntity<ActivityDTO> update(@PathVariable Long id,
                                              @RequestBody ActivityDTO dto) {
        return ResponseEntity.ok(activityService.update(id, dto));
    }

    // ===== HU-10: inactivar =====
    @PatchMapping("/{id}/inactivate")
    public ResponseEntity<Void> inactivate(@PathVariable Long id) {
        activityService.inactivate(id);
        return ResponseEntity.noContent().build();
    }

    // ===== HU-10: eliminar lógico =====
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        activityService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    // ===== Listados =====
    @GetMapping
    public ResponseEntity<List<ActivityDTO>> list() {
        return ResponseEntity.ok(activityService.listActiveOrInactive());
    }

    @GetMapping("/by-process/{processId}")
    public ResponseEntity<List<ActivityDTO>> byProcess(@PathVariable Long processId) {
        return ResponseEntity.ok(activityService.listByProcessId(processId));
    }

    @GetMapping("/by-empresa/{empresaId}")
    public ResponseEntity<List<ActivityDTO>> byEmpresa(@PathVariable Long empresaId) {
        return ResponseEntity.ok(activityService.listByEmpresaId(empresaId));
    }

    @GetMapping("/by-status/{status}")
    public ResponseEntity<List<ActivityDTO>> byProcessStatus(@PathVariable String status) {
        return ResponseEntity.ok(activityService.listByProcessStatus(status));
    }

    @GetMapping("/by-category/{category}")
    public ResponseEntity<List<ActivityDTO>> byCategory(@PathVariable String category) {
        return ResponseEntity.ok(activityService.listByCategory(category));
    }

    @GetMapping("/by-empresa/{empresaId}/status/{status}")
    public ResponseEntity<List<ActivityDTO>> byEmpresaAndStatus(@PathVariable Long empresaId,
                                                                @PathVariable String status) {
        return ResponseEntity.ok(activityService.listByEmpresaAndStatus(empresaId, status));
    }
}