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

import java.util.*;
import java.util.stream.Collectors;

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

    /**
     * Validaciones de negocio generales que NO dependen del tipo de Activity:
     * - Source/Target pertenecen al mismo Process del arco
     * - Source != Target
     * - No duplicar tripleta (process, source, target) salvo que sea el mismo registro en update
     */
    private void validateBusiness(Edge e) {
        if (e.getProcess() == null || e.getSource() == null || e.getTarget() == null) {
            throw new IllegalArgumentException("processId, sourceId y targetId son obligatorios");
        }

        Long pId = e.getProcess().getId();

        if (!Objects.equals(e.getSource().getProcess().getId(), pId) ||
            !Objects.equals(e.getTarget().getProcess().getId(), pId)) {
            throw new IllegalArgumentException("Source y Target deben pertenecer al mismo Process " + pId);
        }

        if (Objects.equals(e.getSource().getId(), e.getTarget().getId())) {
            throw new IllegalArgumentException("Source y Target no pueden ser la misma Activity");
        }

        // Duplicado (tripleta). En update, permitir si es el mismo registro
        boolean duplicated = repo.existsByProcess_IdAndSource_IdAndTarget_Id(
                e.getProcess().getId(), e.getSource().getId(), e.getTarget().getId());

        if (duplicated) {
            if (e.getId() == null) {
                throw new IllegalArgumentException("Ya existe un Edge con esa combinación (process, source, target)");
            } else {
                Edge current = repo.findById(e.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Edge no encontrado: " + e.getId()));
                boolean sameTriplet =
                        Objects.equals(current.getProcess().getId(), e.getProcess().getId()) &&
                        Objects.equals(current.getSource().getId(), e.getSource().getId()) &&
                        Objects.equals(current.getTarget().getId(), e.getTarget().getId());
                if (!sameTriplet) {
                    throw new IllegalArgumentException("Ya existe un Edge con esa combinación (process, source, target)");
                }
            }
        }
    }

    // --------- Utilidades para consistencia al borrar (no aislar nodos) ---------
    private List<Edge> edgesOfProcess(Long processId) {
        return repo.findByProcess_Id(processId);
    }

    /**
     * Construye un mapa de grado total por ActivityId (in+out) dentro de un proceso.
     */
    private Map<Long, Integer> degreeMap(List<Edge> edges) {
        Map<Long, Integer> deg = new HashMap<>();
        for (Edge ed : edges) {
            deg.merge(ed.getSource().getId(), 1, Integer::sum);
            deg.merge(ed.getTarget().getId(), 1, Integer::sum);
        }
        return deg;
    }

    // --------- CRUD ----------
    public EdgeDTO create(EdgeDTO dto) {
        Edge e = new Edge();
        setRefsFromDto(e, dto);

        // Campos simples
        e.setDescription(dto.getDescription());
        e.setStatus(dto.getStatus()); // @PrePersist pondrá ACTIVE si viene null

        validateBusiness(e);

        Edge saved = repo.save(e);
        return toDTO(saved);
    }

    public EdgeDTO update(Long id, EdgeDTO dto) {
        Edge e = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Edge no encontrado: " + id));

        // Merge sin requerir tocar otras capas:
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
        return repo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * Validación de consistencia al borrar (HU-13):
     * - No permite borrar si la eliminación dejaría aislada (grado 0) a la Activity source o target
     *   dentro del proceso. (Esto evita “romper” el diagrama al dejar nodos colgando sin aristas).
     * - El “ajuste automático” visual (re-layout) corresponde al front; aquí garantizamos integridad.
     */
    public void delete(Long id) {
        Edge e = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Edge no encontrado: " + id));

        Long processId = e.getProcess().getId();
        Long sourceId  = e.getSource().getId();
        Long targetId  = e.getTarget().getId();

        List<Edge> edges = edgesOfProcess(processId);
        if (edges.isEmpty()) {
            // Caso raro: el que vamos a borrar es el único del proceso
            repo.delete(e);
            return;
        }

        Map<Long, Integer> deg = degreeMap(edges);

        // Si alguna de las dos actividades tiene grado 1, esa arista es su única conexión.
        // Borrarla la dejaría aislada dentro del proceso.
        if (deg.getOrDefault(sourceId, 0) <= 1) {
            throw new IllegalStateException(
                    "No puedes borrar este arco: dejaría aislada la actividad source (" + sourceId + ") en el proceso " + processId);
        }
        if (deg.getOrDefault(targetId, 0) <= 1) {
            throw new IllegalStateException(
                    "No puedes borrar este arco: dejaría aislada la actividad target (" + targetId + ") en el proceso " + processId);
        }

        repo.delete(e);
        // El back garantiza consistencia; el front se encarga del reacomodo visual.
    }
}
