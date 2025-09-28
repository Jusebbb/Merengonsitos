package com.web.proyecto.services;

import com.web.proyecto.dtos.ProcessDTO;
import com.web.proyecto.entities.Empresa;
import com.web.proyecto.entities.Process;
import com.web.proyecto.entities.Rol;
import com.web.proyecto.repositories.ProcessRepository;
import com.web.proyecto.repositories.RolRepository;
import com.web.proyecto.repositories.EmpresaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProcessService {

    private final ProcessRepository processRepository;
    private final RolRepository rolRepository;
    private final EmpresaRepository empresaRepository;

    // ================== HU-04: Crear proceso ==================
    public ProcessDTO create(ProcessDTO dto) {
    if (dto == null) throw new IllegalArgumentException("Body vacío");
    if (dto.getEmpresaId() == null) throw new IllegalArgumentException("empresaId is required");
    if (dto.getRolId() == null) throw new IllegalArgumentException("rolId is required");

    Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
            .orElseThrow(() -> new IllegalArgumentException("Empresa not found: " + dto.getEmpresaId()));

    Rol rol = rolRepository.findById(dto.getRolId())
            .orElseThrow(() -> new IllegalArgumentException("Role not found: " + dto.getRolId()));

    if (!Objects.equals(rol.getEmpresa().getId(), empresa.getId())) {
        throw new IllegalArgumentException("Role does not belong to the same empresa");
    }

    Process entity = new Process();
    entity.setName(dto.getName().trim());
    entity.setDescription(dto.getDescription());
    entity.setCategory(dto.getCategory());
    entity.setEmpresa(empresa);   // 🔹 ahora seteamos la entidad
    entity.setRol(rol);
    entity.setStatus(normalizeOrDefault(dto.getStatus(), "ACTIVE"));

    Process saved = processRepository.save(entity);
    return toDTO(saved);
}


    // ================== Listas y búsquedas ==================
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<ProcessDTO> listDto() {
        return processRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<ProcessDTO> search(String name, String status, Long empresaId) {
        String st = normalize(status);
        List<Process> res;

        if (empresaId != null && st != null) {
            res = processRepository.findByEmpresaIdAndStatus(empresaId, st);
        } else if (empresaId != null) {
            res = processRepository.findByEmpresaId(empresaId);
        } else if (st != null) {
            res = processRepository.findByStatus(st);
        } else if (name != null && !name.isBlank()) {
            res = processRepository.findByNameContainingIgnoreCase(name.trim());
        } else {
            res = processRepository.findAll();
        }
        return res.stream().map(this::toDTO).toList();
    }

    // ================== Obtener por empresa ==================
    public List<ProcessDTO> getByEmpresaId(Long empresaId) {
        List<Process> procesos = processRepository.findByEmpresaId(
                Objects.requireNonNull(empresaId, "empresaId requerido"));
        if (procesos.isEmpty()) {
            throw new IllegalArgumentException("No processes found for empresaId: " + empresaId);
        }
        return procesos.stream().map(this::toDTO).toList();
    }

    // ================== HU-05: Actualizar en masa por empresa ==================
    public List<ProcessDTO> updateByEmpresaId(Long empresaId, ProcessDTO dto, String user) {
        List<Process> procesos = processRepository.findByEmpresaId(
                Objects.requireNonNull(empresaId, "empresaId requerido"));
        if (procesos.isEmpty()) {
            throw new IllegalArgumentException("No processes found for empresaId: " + empresaId);
        }

        for (Process p : procesos) {
            if (dto.getName() != null && !dto.getName().isBlank()) {
                p.setName(dto.getName().trim());
            }
            if (dto.getDescription() != null) {
                p.setDescription(dto.getDescription());
            }
            if (dto.getCategory() != null) {
                p.setCategory(dto.getCategory());
            }
            if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
                p.setStatus(dto.getStatus().toUpperCase());
            }
            if (dto.getRolId() != null) {
                Rol rol = rolRepository.findById(dto.getRolId())
                        .orElseThrow(() -> new IllegalArgumentException("Role not found: " + dto.getRolId()));
                if (!Objects.equals(rol.getEmpresa().getId(), empresaId)) {
                    throw new IllegalArgumentException("Role does not belong to the same empresa");
                }
                p.setRol(rol);
            }

            p.setUpdatedAt(LocalDateTime.now());
            p.setUpdatedBy(user);
        }

        List<Process> saved = processRepository.saveAll(procesos);
        return saved.stream().map(this::toDTO).toList();
    }

    public void inactivateByEmpresaId(Long empresaId, String user) {
        List<Process> procesos = processRepository.findByEmpresaId(
                Objects.requireNonNull(empresaId, "empresaId requerido"));
        if (procesos.isEmpty()) {
            throw new IllegalArgumentException("No processes found for empresaId: " + empresaId);
        }

        for (Process p : procesos) {
            if (!"INACTIVE".equalsIgnoreCase(p.getStatus())) {
                p.setStatus("INACTIVE");
                p.setUpdatedAt(LocalDateTime.now());
                p.setUpdatedBy(user);
            }
        }
        processRepository.saveAll(procesos);
    }

    // ================== Helpers ==================
    private ProcessDTO toDTO(Process e) {
        Long rolId = (e.getRol() != null) ? e.getRol().getId() : null;
        Long empresaId = (e.getEmpresa() != null) ? e.getEmpresa().getId() : null;
        ProcessDTO dto = new ProcessDTO();
        dto.setId(e.getId());
        dto.setName(e.getName());
        dto.setDescription(e.getDescription());
        dto.setCategory(e.getCategory());
        dto.setStatus(e.getStatus());
        dto.setEmpresaId(empresaId);
        dto.setRolId(rolId);
        dto.setUpdatedAt(e.getUpdatedAt());
        dto.setUpdatedBy(e.getUpdatedBy());
        return dto;
    }


    private String normalize(String status) {
        if (status == null) return null;
        String s = status.trim();
        return s.isEmpty() ? null : s.toUpperCase();
    }

    private String normalizeOrDefault(String status, String def) {
        String s = normalize(status);
        return (s == null) ? def : s;
    }

    // en ProcessService
    public Process findFullById(Long id) {
        return processRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Process not found: " + id));
    }
}
