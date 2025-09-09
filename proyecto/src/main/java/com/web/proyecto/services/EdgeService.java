package com.web.proyecto.services;

import com.web.proyecto.dtos.EdgeDTO;
import com.web.proyecto.entities.Activity;
import com.web.proyecto.entities.Edge;
import com.web.proyecto.entities.Process;
import com.web.proyecto.repositories.ActivityRepository;
import com.web.proyecto.repositories.EdgeRepository;
import com.web.proyecto.repositories.ProcessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EdgeService {

    private final EdgeRepository repo;
    private final ProcessRepository processRepo;
    private final ActivityRepository activityRepo;

    // --------- Mappers ----------
    private EdgeDTO toDTO(Edge e) {
        return EdgeDTO.builder()
                .id(e.getId())
                .processId(e.getProcess().getId())
                .sourceId(e.getSource().getId())
                .targetId(e.getTarget().getId())
                .description(e.getDescription())
                .status(e.getStatus())
                .build();
    }

    // Crea/actualiza a partir de IDs (busca entidades para validar)
    private void setRefsFromDto(Edge e, EdgeDTO d) {
        if (d.getProcessId() != null) {
            Process p = processRepo.findById(d.getProcessId())
                    .orElseThrow(() -> new IllegalArgumentException("Process no encontrado: " + d.getProcessId()));
            e.setProcess(p);
        }
        if (d.getSourceId() != null) {
            Activity a = activityRepo.findById(d.getSourceId())
                    .orElseThrow(() -> new IllegalArgumentException("Activity (source) no encontrada: " + d.getSourceId()));
            e.setSource(a);
        }
        if (d.getTargetId() != null) {
            Activity a = activityRepo.findById(d.getTargetId())
                    .orElseThrow(() -> new IllegalArgumentException("Activity (target) no encontrada: " + d.getTargetId()));
            e.setTarget(a);
        }
    }

    private void validateBusiness(Edge e) {
        Long pId = e.getProcess().getId();
        if (!e.getSource().getProcess().getId().equals(pId) ||
            !e.getTarget().getProcess().getId().equals(pId)) {
            throw new IllegalArgumentException("Source y Target deben pertenecer al mismo Process " + pId);
        }
        if (e.getSource().getId().equals(e.getTarget().getId())) {
            throw new IllegalArgumentException("Source y Target no pueden ser la misma Activity");
        }
        // Duplicado (tripleta)
        if (repo.existsByProcess_IdAndSource_IdAndTarget_Id(
                e.getProcess().getId(), e.getSource().getId(), e.getTarget().getId())) {
            // En update, permite si es el mismo registro
            if (e.getId() == null ||
                repo.findById(e.getId()).map(x ->
                        !(x.getProcess().getId().equals(e.getProcess().getId()) &&
                          x.getSource().getId().equals(e.getSource().getId()) &&
                          x.getTarget().getId().equals(e.getTarget().getId()))
                ).orElse(true)) {
                throw new IllegalArgumentException("Ya existe un Edge con esa combinación (process, source, target)");
            }
        }
    }

    // --------- CRUD ----------
    public EdgeDTO create(EdgeDTO dto) {
        Edge e = new Edge();
        setRefsFromDto(e, dto);
        e.setDescription(dto.getDescription());
        e.setStatus(dto.getStatus()); // @PrePersist pondrá ACTIVE si viene null
        validateBusiness(e);

        Edge saved = repo.save(e);
        return toDTO(saved);
    }

    public EdgeDTO update(Long id, EdgeDTO dto) {
        Edge e = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Edge no encontrado: " + id));

        // Si algún id no viene, se mantiene el actual
        EdgeDTO merged = EdgeDTO.builder()
                .processId(dto.getProcessId() != null ? dto.getProcessId() : e.getProcess().getId())
                .sourceId(dto.getSourceId()   != null ? dto.getSourceId()   : e.getSource().getId())
                .targetId(dto.getTargetId()   != null ? dto.getTargetId()   : e.getTarget().getId())
                .description(dto.getDescription() != null ? dto.getDescription() : e.getDescription())
                .status(dto.getStatus() != null && !dto.getStatus().isBlank() ? dto.getStatus() : e.getStatus())
                .build();

        setRefsFromDto(e, merged);
        e.setDescription(merged.getDescription());
        e.setStatus(merged.getStatus());
        validateBusiness(e);

        return toDTO(repo.save(e));
    }

    @Transactional(readOnly = true)
    public EdgeDTO getById(Long id) {
        return repo.findById(id).map(this::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Edge no encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public List<EdgeDTO> list() {
        return repo.findAll().stream().map(this::toDTO).toList();
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Edge no encontrado: " + id);
        }
        repo.deleteById(id);
    }
}

