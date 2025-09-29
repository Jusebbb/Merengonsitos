package com.web.proyecto.services;

import com.web.proyecto.dtos.ActivityDTO;
import com.web.proyecto.entities.Activity;
import com.web.proyecto.entities.ActivityHistory;
import com.web.proyecto.entities.Process;
import com.web.proyecto.repositories.ActivityHistoryRepository;
import com.web.proyecto.repositories.ActivityRepository;
import com.web.proyecto.repositories.ProcessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final ProcessRepository processRepository;
    private final ActivityHistoryRepository historyRepository;

    /* =================== MAPEOS =================== */

    private ActivityDTO toDTO(Activity a) {
        return ActivityDTO.builder()
                .id(a.getId())
                .name(a.getName())
                .type(a.getType())
                .description(a.getDescription())
                .roleId(a.getRoleId())
                .status(a.getStatus())
                .processId(a.getProcess().getId())
                .build();
    }

    private Activity fromDTOForCreate(ActivityDTO dto, Process p) {
        return Activity.builder()
                .name(dto.getName())
                .type(dto.getType())
                .description(dto.getDescription())
                .roleId(dto.getRoleId())
                .status(dto.getStatus() == null ? "ACTIVE" : dto.getStatus())
                .process(p)
                .build();
    }

    /* =================== CREATE =================== */

    public ActivityDTO create(ActivityDTO dto) {
        if (dto.getName() == null || dto.getName().isBlank())
            throw new IllegalArgumentException("El campo 'name' es obligatorio");
        if (dto.getProcessId() == null)
            throw new IllegalArgumentException("El campo 'processId' es obligatorio");

        Process process = processRepository.findById(dto.getProcessId())
                .orElseThrow(() -> new IllegalArgumentException("Proceso no encontrado con id=" + dto.getProcessId()));

        Activity activity = fromDTOForCreate(dto, process);
        Activity saved = activityRepository.save(activity);

        return toDTO(saved);
    }

    /* =================== UPDATE + HISTORIAL =================== */

    public ActivityDTO update(Long id, ActivityDTO dto) {
        Activity a = activityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Activity no existe: " + id));

        trackChange(id, "name", a.getName(), dto.getName());
        trackChange(id, "type", a.getType(), dto.getType());
        trackChange(id, "description", a.getDescription(), dto.getDescription());
        trackChange(id, "roleId", s(a.getRoleId()), s(dto.getRoleId()));
        trackChange(id, "status", n(a.getStatus()), n(dto.getStatus()));
        trackChange(id, "processId", s(a.getProcess().getId()), s(dto.getProcessId()));

        if (dto.getName() != null) a.setName(dto.getName());
        if (dto.getType() != null) a.setType(dto.getType());
        if (dto.getDescription() != null) a.setDescription(dto.getDescription());
        if (dto.getRoleId() != null) a.setRoleId(dto.getRoleId());
        if (dto.getStatus() != null) a.setStatus(dto.getStatus());

        if (dto.getProcessId() != null && !dto.getProcessId().equals(a.getProcess().getId())) {
            Process p = processRepository.findById(dto.getProcessId())
                    .orElseThrow(() -> new IllegalArgumentException("Process no existe: " + dto.getProcessId()));
            a.setProcess(p);
        }

        return toDTO(activityRepository.save(a));
    }

    /* =================== SOFT DELETE =================== */

    public void inactivate(Long id) {
        Activity a = activityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Activity no existe: " + id));

        if (!"INACTIVE".equalsIgnoreCase(a.getStatus())) {
            trackChange(id, "status", n(a.getStatus()), "INACTIVE");
            a.setStatus("INACTIVE");
            activityRepository.save(a);
        }
    }

    public void softDelete(Long id) {
        Activity a = activityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Activity no existe: " + id));

        if (!"DELETED".equalsIgnoreCase(a.getStatus())) {
            trackChange(id, "status", n(a.getStatus()), "DELETED");
            a.setStatus("DELETED");
            activityRepository.save(a);
        }
    }

    /* =================== QUERIES =================== */

    @Transactional(readOnly = true)
    public List<ActivityDTO> listActiveOrInactive() {
        return activityRepository.findByStatusNot("DELETED")
                .stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<ActivityDTO> listByProcessId(Long processId) {
        return activityRepository.findByProcess_Id(processId).stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<ActivityDTO> listByEmpresaId(Long empresaId) {
        return activityRepository.findByProcess_EmpresaId(empresaId).stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<ActivityDTO> listByProcessStatus(String status) {
        return activityRepository.findByProcess_Status(status).stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<ActivityDTO> listByCategory(String category) {
        return activityRepository.findByProcess_Category(category).stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<ActivityDTO> listByEmpresaAndStatus(Long empresaId, String status) {
        return activityRepository.findByProcess_EmpresaIdAndProcess_Status(empresaId, status)
                .stream().map(this::toDTO).toList();
    }

    /* =================== HELPERS =================== */

    private void trackChange(Long activityId, String field, String oldV, String newV) {
        if (!Objects.equals(n(oldV), n(newV))) {
            historyRepository.save(ActivityHistory.builder()
                    .activityId(activityId)
                    .fieldName(field)
                    .oldValue(oldV)
                    .newValue(newV)
                    .build());
        }
    }

    private String n(String v) { return v == null ? "" : v; }
    private String s(Object v) { return v == null ? null : String.valueOf(v); }
}