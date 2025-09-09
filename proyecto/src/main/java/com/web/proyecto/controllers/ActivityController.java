package com.web.proyecto.controllers;

import com.web.proyecto.dtos.ActivityDTO;
import com.web.proyecto.services.ActivityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/activities")
public class ActivityController {

    private final ActivityService activityService;

    /* -------- CRUD -------- */

    @PostMapping
    public ResponseEntity<ActivityDTO> create(@Valid @RequestBody ActivityDTO dto){
        return new ResponseEntity<>(activityService.create(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ActivityDTO>> listAll(){
        return ResponseEntity.ok(activityService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityDTO> getById(@PathVariable Long id){
        return ResponseEntity.ok(activityService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActivityDTO> update(@PathVariable Long id, @Valid @RequestBody ActivityDTO dto){
        return ResponseEntity.ok(activityService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        activityService.delete(id);
    }

    /* -------- Filtros paralelos a Process -------- */

    // Por id de proceso
    @GetMapping("/process/{processId}")
    public ResponseEntity<List<ActivityDTO>> listByProcess(@PathVariable Long processId){
        return ResponseEntity.ok(activityService.listByProcess(processId));
    }

    // Por empresa (process.empresaId)
    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<ActivityDTO>> listByEmpresa(@PathVariable Long empresaId){
        return ResponseEntity.ok(activityService.listByEmpresa(empresaId));
    }

    // Por status del proceso
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ActivityDTO>> listByProcessStatus(@PathVariable String status){
        return ResponseEntity.ok(activityService.listByProcessStatus(status));
    }

    // Por categoría del proceso
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ActivityDTO>> listByProcessCategory(@PathVariable String category){
        return ResponseEntity.ok(activityService.listByProcessCategory(category));
    }

    // Combinado empresa + status
    @GetMapping("/empresa/{empresaId}/status/{status}")
    public ResponseEntity<List<ActivityDTO>> listByEmpresaAndStatus(@PathVariable Long empresaId, @PathVariable String status){
        return ResponseEntity.ok(activityService.listByEmpresaAndStatus(empresaId, status));
    }
}
