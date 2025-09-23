package com.web.proyecto.services;

import com.web.proyecto.dtos.ProcessDTO;
import com.web.proyecto.entities.Process;
import com.web.proyecto.entities.ProcessHistory;
import com.web.proyecto.entities.ProcessStatus;
import com.web.proyecto.repositories.ProcessRepository;
import com.web.proyecto.repositories.RolRepository;
import com.web.proyecto.repositories.ProcessHistoryRepository;
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
                          ProcessHistoryRepository historyRepository, RolRepository rolRepository) {
        this.processRepository = processRepository;
        this.historyRepository = historyRepository;
        this.rolRepository = rolRepository;
    }

    // Crear proceso
    public ProcessDTO create(ProcessDTO dto) {
        if (dto.getEmpresaId() == null) {
            throw new IllegalArgumentException("empresaId is required");
        }
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("name is required");
        }

        Process entity = new Process();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setCategory(dto.getCategory());
        entity.setStatus(dto.getStatus() == null ? ProcessStatus.DRAFT : dto.getStatus());
        entity.setEmpresaId(dto.getEmpresaId());

        Process saved = processRepository.save(entity);

        return toDTO(saved);
    }

    // Obtener proceso por id
    @Transactional(readOnly = true)
    public Process get(Long id) {
        return processRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Process not found: " + id));
    }

    // Listar todos
    @Transactional(readOnly = true)
    public List<Process> list() {
        return processRepository.findAll();
    }

    // Soft delete (HU-06)
    public void deleteByEmpresaId(Long empresaId) {
        List<Process> procesos = processRepository.findByEmpresaId(empresaId);
        if (procesos.isEmpty()) {
            throw new IllegalArgumentException("No processes found for empresaId: " + empresaId);
        }
        for (Process p : procesos) {
            p.setStatus(ProcessStatus.INACTIVE);
        }
        processRepository.saveAll(procesos);
    }

    // Obtener procesos por empresa
    @Transactional(readOnly = true)
    public Object getByEmpresaId(Long empresaId) {
        List<Process> procesos = processRepository.findByEmpresaId(empresaId);
        if (procesos.isEmpty()) {
            throw new IllegalArgumentException("No processes found for empresaId: " + empresaId);
        }
        return procesos.stream().map(this::toDTO).toList();
    }

    // Actualizar procesos con historial (HU-05)
    public Object updateByEmpresaId(Long empresaId, ProcessDTO dto) {
        List<Process> procesos = processRepository.findByEmpresaId(empresaId);
        if (procesos.isEmpty()) {
            throw new IllegalArgumentException("No processes found for empresaId: " + empresaId);
        }

        for (Process p : procesos) {
            if (dto.getName() != null && !dto.getName().equals(p.getName())) {
                saveHistory(p.getId(), "name", p.getName(), dto.getName());
                p.setName(dto.getName());
            }
            if (dto.getDescription() != null && !dto.getDescription().equals(p.getDescription())) {
                saveHistory(p.getId(), "description", p.getDescription(), dto.getDescription());
                p.setDescription(dto.getDescription());
            }
            if (dto.getCategory() != null && !dto.getCategory().equals(p.getCategory())) {
                saveHistory(p.getId(), "category", p.getCategory(), dto.getCategory());
                p.setCategory(dto.getCategory());
            }
            if (dto.getStatus() != null && dto.getStatus() != p.getStatus()) {
                saveHistory(p.getId(), "status", p.getStatus().name(), dto.getStatus().name());
                p.setStatus(dto.getStatus());
            }
        }

        List<Process> saved = processRepository.saveAll(procesos);
        return saved.stream().map(this::toDTO).toList();
    }

    private void saveHistory(Long processId, String field, String oldValue, String newValue) {
        ProcessHistory history = new ProcessHistory();
        history.setProcessId(processId);
        history.setField(field);
        history.setOldValue(oldValue);
        history.setNewValue(newValue);
        history.setChangedAt(LocalDateTime.now());
        historyRepository.save(history);
    }

    private ProcessDTO toDTO(Process entity) {
        return new ProcessDTO(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getCategory(),
                entity.getStatus(),
                entity.getEmpresaId(),
                entity.getId()
        );
    }
}
