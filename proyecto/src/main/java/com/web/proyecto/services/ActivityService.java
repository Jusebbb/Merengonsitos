package com.web.proyecto.services;

import com.web.proyecto.dtos.ActivityDTO;
import com.web.proyecto.entities.Activity;
import com.web.proyecto.entities.Process;
import com.web.proyecto.repositories.ActivityRepository;
import com.web.proyecto.repositories.ProcessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final ProcessRepository processRepository;

    /* ---------- Mappers ---------- */
    private ActivityDTO toDTO(Activity a){
        return ActivityDTO.builder()
                .id(a.getId())
                .name(a.getName())
                .processId(a.getProcess().getId())
                .build();
    }

    private Activity fromDTOForCreate(ActivityDTO dto, Process p){
        return Activity.builder()
                .name(dto.getName())
                .process(p)
                .build();
    }

    /* ---------- CRUD ---------- */

    public ActivityDTO create(ActivityDTO dto){
        Process p = processRepository.findById(dto.getProcessId())
                .orElseThrow(() -> new IllegalArgumentException("Process no existe: " + dto.getProcessId()));

        if (activityRepository.existsByNameIgnoreCaseAndProcess_Id(dto.getName(), p.getId())){
            throw new IllegalArgumentException("Ya existe una Activity con ese name en el proceso");
        }

        Activity saved = activityRepository.save(fromDTOForCreate(dto, p));
        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<ActivityDTO> listAll(){
        return activityRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public ActivityDTO getById(Long id){
        Activity a = activityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Activity no existe: " + id));
        return toDTO(a);
    }

    public ActivityDTO update(Long id, ActivityDTO dto){
        Activity a = activityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Activity no existe: " + id));

        // Si cambia el nombre, validar duplicado dentro del mismo proceso:
        if (dto.getName() != null &&
                activityRepository.existsByNameIgnoreCaseAndProcess_Id(dto.getName(), a.getProcess().getId())){
            throw new IllegalArgumentException("Nombre duplicado en el proceso");
        }
        if (dto.getName() != null) a.setName(dto.getName());

        // Si cambia de proceso:
        if (dto.getProcessId() != null && !dto.getProcessId().equals(a.getProcess().getId())){
            Process p = processRepository.findById(dto.getProcessId())
                    .orElseThrow(() -> new IllegalArgumentException("Process no existe: " + dto.getProcessId()));
            // Verificar duplicado en el nuevo proceso
            if (activityRepository.existsByNameIgnoreCaseAndProcess_Id(a.getName(), p.getId())){
                throw new IllegalArgumentException("Nombre duplicado en el nuevo proceso");
            }
            a.setProcess(p);
        }

        return toDTO(activityRepository.save(a));
    }

    public void delete(Long id){
        if (!activityRepository.existsById(id)){
            throw new IllegalArgumentException("Activity no existe: " + id);
        }
        activityRepository.deleteById(id);
    }

    /* ---------- Filtros paralelos a Process ---------- */

    @Transactional(readOnly = true)
    public List<ActivityDTO> listByProcess(Long processId){
        return activityRepository.findByProcess_Id(processId).stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<ActivityDTO> listByEmpresa(Long empresaId){
        return activityRepository.findByProcess_EmpresaId(empresaId).stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<ActivityDTO> listByProcessStatus(String status){
        return activityRepository.findByProcess_Status(status).stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<ActivityDTO> listByProcessCategory(String category){
        return activityRepository.findByProcess_Category(category).stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<ActivityDTO> listByEmpresaAndStatus(Long empresaId, String status){
        return activityRepository.findByProcess_EmpresaIdAndProcess_Status(empresaId, status)
                .stream().map(this::toDTO).toList();
    }
}
