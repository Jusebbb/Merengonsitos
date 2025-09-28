package com.web.proyecto.services;

import com.web.proyecto.dtos.EdgeDTO;
import com.web.proyecto.entities.Activity;
import com.web.proyecto.entities.Edge;
import com.web.proyecto.entities.Process;
import com.web.proyecto.repositories.ActivityRepository;
import com.web.proyecto.repositories.EdgeRepository;
import com.web.proyecto.repositories.ProcessRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class EdgeService {

    private final EdgeRepository edgeRepo;
    private final ProcessRepository processRepo;
    private final ActivityRepository activityRepo;

    public List<EdgeDTO> listAll() {
        return edgeRepo.findAll().stream().map(this::toDTO).toList();
    }

    public List<EdgeDTO> listByProcess(Long processId) {
        return edgeRepo.findByProcess_Id(processId).stream().map(this::toDTO).toList();
    }

    public EdgeDTO getById(Long id) {
        return edgeRepo.findById(id).map(this::toDTO)
                .orElseThrow(() -> new NoSuchElementException("Edge not found: " + id));
    }

    public EdgeDTO create(EdgeDTO dto) {
        if (dto.getProcessId() == null) throw new IllegalArgumentException("processId is required");
        if (dto.getSourceId() == null)  throw new IllegalArgumentException("sourceId is required");
        if (dto.getTargetId() == null)  throw new IllegalArgumentException("targetId is required");
        if (Objects.equals(dto.getSourceId(), dto.getTargetId()))
            throw new IllegalArgumentException("sourceId and targetId must be different");

        Process p = processRepo.findById(dto.getProcessId())
                .orElseThrow(() -> new NoSuchElementException("Process not found: " + dto.getProcessId()));
        Activity s = activityRepo.findById(dto.getSourceId())
                .orElseThrow(() -> new NoSuchElementException("Activity (source) not found: " + dto.getSourceId()));
        Activity t = activityRepo.findById(dto.getTargetId())
                .orElseThrow(() -> new NoSuchElementException("Activity (target) not found: " + dto.getTargetId()));

        if (!Objects.equals(s.getProcess().getId(), p.getId()))
            throw new IllegalArgumentException("sourceId does not belong to process " + p.getId());
        if (!Objects.equals(t.getProcess().getId(), p.getId()))
            throw new IllegalArgumentException("targetId does not belong to process " + p.getId());

        // Idempotente: si ya existe, devuelve el existente (no 500, no 409)
        if (edgeRepo.existsByProcess_IdAndSource_IdAndTarget_Id(p.getId(), s.getId(), t.getId())) {
            return edgeRepo.findByProcess_Id(p.getId()).stream()
                    .filter(e -> e.getSource().getId().equals(s.getId()) && e.getTarget().getId().equals(t.getId()))
                    .findFirst().map(this::toDTO).orElseThrow();
        }

        Edge e = new Edge();
        e.setProcess(p);
        e.setSource(s);
        e.setTarget(t);
        e.setDescription(trim45(dto.getDescription()));
        e.setStatus(normalizeOrDefault(dto.getStatus(), "ACTIVE"));

        return toDTO(edgeRepo.save(e));
    }

    public EdgeDTO update(Long id, EdgeDTO dto) {
        Edge e = edgeRepo.findById(id).orElseThrow(() -> new NoSuchElementException("Edge not found: " + id));

        if (dto.getProcessId() != null && !dto.getProcessId().equals(e.getProcess().getId())) {
            Process p = processRepo.findById(dto.getProcessId())
                    .orElseThrow(() -> new NoSuchElementException("Process not found: " + dto.getProcessId()));
            e.setProcess(p);
        }
        if (dto.getSourceId() != null) {
            Activity s = activityRepo.findById(dto.getSourceId())
                    .orElseThrow(() -> new NoSuchElementException("Activity (source) not found: " + dto.getSourceId()));
            e.setSource(s);
        }
        if (dto.getTargetId() != null) {
            Activity t = activityRepo.findById(dto.getTargetId())
                    .orElseThrow(() -> new NoSuchElementException("Activity (target) not found: " + dto.getTargetId()));
            e.setTarget(t);
        }

        Long pid = e.getProcess().getId();
        if (Objects.equals(e.getSource().getId(), e.getTarget().getId()))
            throw new IllegalArgumentException("sourceId and targetId must be different");
        if (!Objects.equals(e.getSource().getProcess().getId(), pid) ||
            !Objects.equals(e.getTarget().getProcess().getId(), pid))
            throw new IllegalArgumentException("source/target must belong to process " + pid);

        // Evita duplicado contra otros edges
        if (edgeRepo.existsByProcess_IdAndSource_IdAndTarget_Id(pid, e.getSource().getId(), e.getTarget().getId())) {
            boolean otherExists = edgeRepo.findByProcess_Id(pid).stream()
                .anyMatch(x -> !x.getId().equals(e.getId())
                        && x.getSource().getId().equals(e.getSource().getId())
                        && x.getTarget().getId().equals(e.getTarget().getId()));
            if (otherExists) throw new IllegalArgumentException("Edge already exists for (processId, sourceId, targetId)");
        }

        if (dto.getDescription() != null) e.setDescription(trim45(dto.getDescription()));
        if (dto.getStatus() != null)      e.setStatus(normalize(dto.getStatus()));

        return toDTO(edgeRepo.save(e));
    }

    public void delete(Long id) {
        if (!edgeRepo.existsById(id)) throw new NoSuchElementException("Edge not found: " + id);
        edgeRepo.deleteById(id);
    }

    private String trim45(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.length() <= 45 ? t : t.substring(0, 45);
    }
    private String normalize(String s) { return (s == null || s.isBlank()) ? null : s.trim().toUpperCase(); }
    private String normalizeOrDefault(String s, String def) { String n = normalize(s); return n == null ? def : n; }

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
}
