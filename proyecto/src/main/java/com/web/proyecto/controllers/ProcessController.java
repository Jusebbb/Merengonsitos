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

    // ====== Crear proceso (HU-04) ======
    @PostMapping
    public ResponseEntity<ProcessDTO> create(@RequestBody ProcessDTO dto) {
        ProcessDTO created = service.create(dto);
        return ResponseEntity
                .created(URI.create("/api/processes/" + created.getId()))
                .body(created);
    }

    // ====== Listado simple ======
    @GetMapping
    public ResponseEntity<List<ProcessDTO>> list() {
        return ResponseEntity.ok(service.listDto());
    }

    // ====== Búsqueda con filtros (HU-07) ======
    //   GET /api/processes/search?empresaId=1&status=ACTIVE
    //   GET /api/processes/search?name=onboard
    @GetMapping("/search")
    public ResponseEntity<List<ProcessDTO>> search(@RequestParam(required = false) String name,
                                                   @RequestParam(required = false) String status,
                                                   @RequestParam(required = false) Long empresaId) {
        return ResponseEntity.ok(service.search(name, status, empresaId));
    }

    // ====== Vista completa (HU-07) ======
    //   GET /api/processes/{id}/full
    @GetMapping("/{id}/full")
    public ResponseEntity<Process> getFullView(@PathVariable Long id) {
        return ResponseEntity.ok(service.findFullById(id));
    }

    // ====== Obtener procesos por empresa ======
    @GetMapping("/{empresaId}")
    public ResponseEntity<List<ProcessDTO>> getByEmpresaId(@PathVariable Long empresaId) {
        return ResponseEntity.ok(service.getByEmpresaId(empresaId));
    }

    // ====== Actualizar en masa por empresa (HU-05 - auditoría ligera: updatedAt/updatedBy) ======
    @PutMapping("/{empresaId}")
    public ResponseEntity<List<ProcessDTO>> updateByEmpresaId(@PathVariable Long empresaId,
                                                              @RequestBody ProcessDTO dto,
                                                              @RequestHeader(value = "X-User", required = false) String user) {
        return ResponseEntity.ok(service.updateByEmpresaId(empresaId, dto, user));
    }

    // ====== Soft delete por empresa (HU-06) ======
    // Conserva DELETE pero realiza inactivate (status=INACTIVE) en el service.
    @DeleteMapping("/{empresaId}")
    public ResponseEntity<Void> deleteByEmpresaId(@PathVariable Long empresaId,
                                                  @RequestHeader(value = "X-User", required = false) String user) {
        service.inactivateByEmpresaId(empresaId, user);
        return ResponseEntity.noContent().build();
    }
}