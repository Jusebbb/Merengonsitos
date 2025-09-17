package com.web.proyecto.services;

import com.web.proyecto.dtos.ProcessDTO;
import com.web.proyecto.entities.Process;
import com.web.proyecto.entities.Rol;
import com.web.proyecto.repositories.ProcessRepository;
import com.web.proyecto.repositories.RolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProcessService {

    private final ProcessRepository processRepository;
    private final RolRepository rolRepository; // <-- NUEVO

    public ProcessService(ProcessRepository processRepository, RolRepository rolRepository) {
        this.processRepository = processRepository;
        this.rolRepository = rolRepository; // <-- NUEVO
    }

    public Process create(String name) {
        Process p = new Process();
        p.setName(name);
        // OJO: si Process.rol es NOT NULL, este método necesitará asignar un rol por defecto
        return processRepository.save(p);
    }

    @Transactional(readOnly = true)
    public Process get(Long id) {
        return processRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Process not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<Process> list() {
        return processRepository.findAll();
    }

    public Process update(Long id, String name) {
        Process p = get(id);
        p.setName(name);
        return processRepository.save(p);
    }

    public void deleteByEmpresaId(Long empresaId) {
        List<Process> procesos = processRepository.findByEmpresaId(empresaId);
        if (procesos.isEmpty()) {
            throw new IllegalArgumentException("No processes found for empresaId: " + empresaId);
        }
        processRepository.deleteAll(procesos);
    }

    @Transactional(readOnly = true)
    public Object getByEmpresaId(Long empresaId) {
        List<Process> procesos = processRepository.findByEmpresaId(empresaId);
        if (procesos.isEmpty()) {
            throw new IllegalArgumentException("No processes found for empresaId: " + empresaId);
        }
        return procesos.stream().map(p -> {
            ProcessDTO dto = new ProcessDTO();
            dto.setId(p.getId());
            dto.setName(p.getName());
            dto.setDescription(p.getDescription());
            dto.setCategory(p.getCategory());
            dto.setStatus(p.getStatus());
            dto.setEmpresaId(p.getEmpresaId());
            dto.setRolId(p.getRol() != null ? p.getRol().getId() : null); // <-- NUEVO
            return dto;
        }).toList();
    }

    public Object updateByEmpresaId(Long empresaId, ProcessDTO dto) {
        List<Process> procesos = processRepository.findByEmpresaId(empresaId);
        if (procesos.isEmpty()) {
            throw new IllegalArgumentException("No processes found for empresaId: " + empresaId);
        }

        // Si viene rolId, resolver y validar que pertenezca a la misma empresa
        Rol rolResolved = null;
        if (dto.getRolId() != null) {
            rolResolved = rolRepository.findById(dto.getRolId())
                    .orElseThrow(() -> new IllegalArgumentException("Role not found: " + dto.getRolId()));
            if (!rolResolved.getEmpresa().getId().equals(empresaId)) {
                throw new IllegalArgumentException("Role does not belong to the same empresa");
            }
        }

        for (Process p : procesos) {
            if (dto.getName() != null)        p.setName(dto.getName());
            if (dto.getDescription() != null) p.setDescription(dto.getDescription());
            if (dto.getCategory() != null)    p.setCategory(dto.getCategory());
            if (dto.getStatus() != null)      p.setStatus(dto.getStatus());
            if (rolResolved != null)          p.setRol(rolResolved); // <-- NUEVO (update masivo de rol)
        }

        List<Process> saved = processRepository.saveAll(procesos);

        return saved.stream().map(p -> {
            ProcessDTO out = new ProcessDTO();
            out.setId(p.getId());
            out.setName(p.getName());
            out.setDescription(p.getDescription());
            out.setCategory(p.getCategory());
            out.setStatus(p.getStatus());
            out.setEmpresaId(p.getEmpresaId());
            out.setRolId(p.getRol() != null ? p.getRol().getId() : null); // <-- NUEVO
            return out;
        }).toList();
    }

    public ProcessDTO create(ProcessDTO dto) {
        if (dto.getEmpresaId() == null) {
            throw new IllegalArgumentException("empresaId is required");
        }
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        if (dto.getRolId() == null) {
            throw new IllegalArgumentException("rolId is required"); // <-- NUEVO
        }

        // Resolver rol y validar que pertenezca a la misma empresa
        Rol rol = rolRepository.findById(dto.getRolId())
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + dto.getRolId()));
        if (!rol.getEmpresa().getId().equals(dto.getEmpresaId())) {
            throw new IllegalArgumentException("Role does not belong to the same empresa");
        }

        Process entity = new Process();
        entity.setId(null);
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setCategory(dto.getCategory());
        entity.setStatus((dto.getStatus() == null || dto.getStatus().isBlank()) ? "DRAFT" : dto.getStatus());
        entity.setEmpresaId(dto.getEmpresaId());
        entity.setRol(rol); // <-- NUEVO

        Process saved = processRepository.save(entity);

        ProcessDTO res = new ProcessDTO();
        res.setId(saved.getId());
        res.setName(saved.getName());
        res.setDescription(saved.getDescription());
        res.setCategory(saved.getCategory());
        res.setStatus(saved.getStatus());
        res.setEmpresaId(saved.getEmpresaId());
        res.setRolId(saved.getRol() != null ? saved.getRol().getId() : null); // <-- NUEVO
        return res;
    }
}
