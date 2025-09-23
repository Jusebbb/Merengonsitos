package com.web.proyecto.services;

import com.web.proyecto.dtos.ProcessDTO;
import com.web.proyecto.entities.Process;
import com.web.proyecto.entities.ProcessHistory;
import com.web.proyecto.entities.ProcessStatus;
import com.web.proyecto.entities.Rol;
import com.web.proyecto.repositories.ProcessHistoryRepository;
import com.web.proyecto.repositories.ProcessRepository;
import com.web.proyecto.repositories.RolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ProcessService {

    private final ProcessRepository processRepository;
    private final ProcessHistoryRepository historyRepository;
    private final RolRepository rolRepository;

    public ProcessService(ProcessRepository processRepository,
                          ProcessHistoryRepository historyRepository,
                          RolRepository rolRepository) {
        this.processRepository = processRepository;
        this.historyRepository = historyRepository;
        this.rolRepository = rolRepository;
    }

    // ====== Crear proceso (con validación de Rol y estado por defecto) ======
    public ProcessDTO create(ProcessDTO dto) {
        if (dto.getEmpresaId() == null) {
            throw new IllegalArgumentException("empresaId is required");
        }
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        if (dto.getRolId() == null) {
            throw new IllegalArgumentException("rolId is required");
        }

        Rol rol = rolRepository.findById(dto.getRolId())
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + dto.getRolId()));
        if (!rol.getEmpresa().getId().equals(dto.getEmpresaId())) {
            throw new IllegalArgumentException("Role does not belong to the same empresa");
        }

        Process entity = new Process();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setCategory(dto.getCategory());
        entity.setStatus(dto.getStatus() == null ? ProcessStatus.DRAFT : dto.getStatus());
        entity.setEmpresaId(dto.getEmpresaId());
        entity.setRol(rol);

        Process saved = processRepository.save(entity);
        return toDTO(saved);
    }

    // Overload simple (opcional): crear solo con nombre
    public Process create(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        Process p = new Process();
        p.setName(name);
        p.setStatus(ProcessStatus.DRAFT);
        return processRepository.save(p);
    }

    // ====== Lecturas ======
    @Transactional(readOnly = true)
    public Process get(Long id) {
        return processRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Process not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<Process> list() {
        return processRepository.findAll();
    }

    // ====== Update simple por id (solo nombre) ======
    public Process update(Long id, String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        Process p = get(id);
        if (!name.equals(p.getName())) {
            saveHistory(p.getId(), "name", p.getName(), name);
            p.setName(name);
        }
        return processRepository.save(p);
    }

    // ====== Soft delete por empresa (HU-06) ======
    public void deleteByEmpresaId(Long empresaId) {
        List<Process> procesos = processRepository.findByEmpresaId(empresaId);
        if (procesos.isEmpty()) {
            throw new IllegalArgumentException("No processes found for empresaId: " + empresaId);
        }
        for (Process p : procesos) {
            if (p.getStatus() != ProcessStatus.INACTIVE) {
                saveHistory(p.getId(), "status",
                        p.getStatus() == null ? null : p.getStatus().name(),
                        ProcessStatus.INACTIVE.name());
                p.setStatus(ProcessStatus.INACTIVE);
            }
        }
        processRepository.saveAll(procesos);
    }

    // ====== Obtener por empresa ======
    @Transactional(readOnly = true)
    public List<ProcessDTO> getByEmpresaId(Long empresaId) {
        List<Process> procesos = processRepository.findByEmpresaId(empresaId);
        if (procesos.isEmpty()) {
            throw new IllegalArgumentException("No processes found for empresaId: " + empresaId);
        }
        return procesos.stream().map(this::toDTO).toList();
    }

    // ====== Actualizar en masa por empresa con historial (HU-05) ======
    public List<ProcessDTO> updateByEmpresaId(Long empresaId, ProcessDTO dto) {
        List<Process> procesos = processRepository.findByEmpresaId(empresaId);
        if (procesos.isEmpty()) {
            throw new IllegalArgumentException("No processes found for empresaId: " + empresaId);
        }

        // Resolver y validar rol si se quiere cambiar
        Rol rolResolved = null;
        if (dto.getRolId() != null) {
            rolResolved = rolRepository.findById(dto.getRolId())
                    .orElseThrow(() -> new IllegalArgumentException("Role not found: " + dto.getRolId()));
            if (!rolResolved.getEmpresa().getId().equals(empresaId)) {
                throw new IllegalArgumentException("Role does not belong to the same empresa");
            }
        }

        for (Process p : procesos) {
            // name
            if (dto.getName() != null && !dto.getName().equals(p.getName())) {
                saveHistory(p.getId(), "name", p.getName(), dto.getName());
                p.setName(dto.getName());
            }
            // description
            if (dto.getDescription() != null && !dto.getDescription().equals(p.getDescription())) {
                saveHistory(p.getId(), "description", p.getDescription(), dto.getDescription());
                p.setDescription(dto.getDescription());
            }
            // category
            if (dto.getCategory() != null && !dto.getCategory().equals(p.getCategory())) {
                saveHistory(p.getId(), "category", p.getCategory(), dto.getCategory());
                p.setCategory(dto.getCategory());
            }
            // status (enum)
            if (dto.getStatus() != null && dto.getStatus() != p.getStatus()) {
                saveHistory(p.getId(), "status",
                        p.getStatus() == null ? null : p.getStatus().name(),
                        dto.getStatus().name());
                p.setStatus(dto.getStatus());
            }
            // rol
            if (rolResolved != null) {
                Long oldRolId = p.getRol() != null ? p.getRol().getId() : null;
                Long newRolId = rolResolved.getId();
                if ((oldRolId == null && newRolId != null) ||
                        (oldRolId != null && !oldRolId.equals(newRolId))) {
                    saveHistory(p.getId(), "rolId",
                            oldRolId == null ? null : oldRolId.toString(),
                            newRolId.toString());
                    p.setRol(rolResolved);
                }
            }
        }

        List<Process> saved = processRepository.saveAll(procesos);
        return saved.stream().map(this::toDTO).toList();
    }

    // ====== Helpers ======
    private void saveHistory(Long processId, String field, String oldValue, String newValue) {
        ProcessHistory h = new ProcessHistory();
        h.setProcessId(processId);
        h.setField(field);
        h.setOldValue(oldValue);
        h.setNewValue(newValue);
        h.setChangedAt(LocalDateTime.now());
        historyRepository.save(h);
    }

    private ProcessDTO toDTO(Process e) {
        Long rolId = (e.getRol() != null) ? e.getRol().getId() : null;
        return new ProcessDTO(
                e.getId(),
                e.getName(),
                e.getDescription(),
                e.getCategory(),
                e.getStatus(),
                e.getEmpresaId(),
                rolId
        );
    }
}
