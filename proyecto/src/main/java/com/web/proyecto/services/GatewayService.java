package com.web.proyecto.services;

import com.web.proyecto.dtos.GatewayDTO;
import com.web.proyecto.entities.Gateway;
import com.web.proyecto.entities.Process;
import com.web.proyecto.repositories.GatewayRepository;
import com.web.proyecto.repositories.ProcessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GatewayService {

    private final GatewayRepository gatewayRepo;
    private final ProcessRepository processRepo;

    // ---------- Mapeos ----------
    private GatewayDTO toDTO(Gateway e) {
        Long processId = (e.getProcess() != null) ? e.getProcess().getId() : null;
        return GatewayDTO.builder()
                .id(e.getId())
                .name(e.getName())
                .type(e.getType())
                .condition(e.getCondition())
                .status(e.getStatus())
                .description(e.getDescription())
                .processId(processId)                 // <- del objeto Process
                .sourceActivityId(e.getSourceActivityId())
                .targetActivityId(e.getTargetActivityId())
                .build();
    }

    private Gateway toEntity(GatewayDTO d, Process process) {
        return Gateway.builder()
                .id(d.getId())
                .name(d.getName())
                .type(d.getType())
                .condition(d.getCondition())
                .status(d.getStatus())
                .description(d.getDescription())
                .process(process)                     // <- asignamos la entidad
                .sourceActivityId(d.getSourceActivityId())
                .targetActivityId(d.getTargetActivityId())
                .build();
    }

    // ---------- CRUD ----------
    public GatewayDTO create(GatewayDTO dto) {
        if (dto.getType() == null || dto.getStatus() == null ||
            dto.getProcessId() == null || dto.getSourceActivityId() == null || dto.getTargetActivityId() == null) {
            throw new IllegalArgumentException("Faltan campos obligatorios del Gateway.");
        }

        Process process = processRepo.findById(dto.getProcessId())
                .orElseThrow(() -> new IllegalArgumentException("Proceso no encontrado: " + dto.getProcessId()));

        Gateway saved = gatewayRepo.save(toEntity(dto, process));
        return toDTO(saved);
    }

    public GatewayDTO update(Long id, GatewayDTO dto) {
        Gateway e = gatewayRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Gateway no encontrado: " + id));

        // Reasignar proceso si mandan processId
        if (dto.getProcessId() != null) {
            Process process = processRepo.findById(dto.getProcessId())
                    .orElseThrow(() -> new IllegalArgumentException("Proceso no encontrado: " + dto.getProcessId()));
            e.setProcess(process);
        }

        if (dto.getName() != null)            e.setName(dto.getName());
        if (dto.getType() != null)            e.setType(dto.getType());
        if (dto.getCondition() != null)       e.setCondition(dto.getCondition());
        if (dto.getStatus() != null)          e.setStatus(dto.getStatus());
        if (dto.getDescription() != null)     e.setDescription(dto.getDescription());
        if (dto.getSourceActivityId() != null)e.setSourceActivityId(dto.getSourceActivityId());
        if (dto.getTargetActivityId() != null)e.setTargetActivityId(dto.getTargetActivityId());

        return toDTO(gatewayRepo.save(e));
    }

    @Transactional(readOnly = true)
    public GatewayDTO getById(Long id) {
        return gatewayRepo.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Gateway no encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public List<GatewayDTO> list() {
        return gatewayRepo.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<GatewayDTO> listByProcessId(Long processId) {
        return gatewayRepo.findByProcessId(processId).stream().map(this::toDTO).toList();
    }

    public void delete(Long id) {
        if (!gatewayRepo.existsById(id)) {
            throw new IllegalArgumentException("Gateway no encontrado: " + id);
        }
        gatewayRepo.deleteById(id);
    }
}