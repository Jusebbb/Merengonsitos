package com.web.proyecto.controllers;

import com.web.proyecto.dtos.GatewayDTO;
import com.web.proyecto.services.GatewayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/gateways")
@RequiredArgsConstructor
@CrossOrigin
public class GatewayController {

    private final GatewayService service;

    @PostMapping
    public ResponseEntity<GatewayDTO> create(@Validated @RequestBody GatewayDTO dto) {
        GatewayDTO created = service.create(dto);
        return ResponseEntity.created(URI.create("/api/gateways/" + created.getId())).body(created);
    }

    @GetMapping
    public ResponseEntity<List<GatewayDTO>> list() {
        return ResponseEntity.ok(service.list());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GatewayDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GatewayDTO> update(@PathVariable Long id, @Validated @RequestBody GatewayDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
