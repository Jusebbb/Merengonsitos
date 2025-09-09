package com.web.proyecto.services;

import com.web.proyecto.dtos.GatewayDTO;
import com.web.proyecto.entities.Gateway;
import com.web.proyecto.repositories.GatewayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GatewayService {

    private final GatewayRepository repo;

    // ---- Mapeos ----
    private GatewayDTO toDTO(Gateway e) {
        return GatewayDTO.builder()
                .id(e.getId())
                .name(e.getName())
                .type(e.getType())
                .condition(e.getCondition())      // si en la entidad la columna es condition_expr, el atributo sigue siendo condition
                .status(e.getStatus())
                .description(e.getDescription())
                .processId(e.getProcessId())
                .sourceActivityId(e.getSourceActivityId())
                .targetActivityId(e.getTargetActivityId())
                .build();
    }

    private Gateway toEntity(GatewayDTO d) {
        return Gateway.builder()
                .id(d.getId())
                .name(d.getName())
                .type(d.getType())
                .condition(d.getCondition())
                .status(d.getStatus())
                .description(d.getDescription())
                .processId(d.getProcessId())
                .sourceActivityId(d.getSourceActivityId())
                .targetActivityId(d.getTargetActivityId())
                .build();
    }

    // ---- CRUD ----
    public GatewayDTO create(GatewayDTO dto) {
        // (opcional) valida campos obligatorios si no usas @Valid en el controller
        if (dto.getType() == null || dto.getStatus() == null ||
            dto.getProcessId() == null || dto.getSourceActivityId() == null || dto.getTargetActivityId() == null) {
            throw new IllegalArgumentException("Faltan campos obligatorios del Gateway.");
        }
        Gateway saved = repo.save(toEntity(dto));
        return toDTO(saved);
    }

    public GatewayDTO update(Long id, GatewayDTO dto) {
        Gateway e = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Gateway no encontrado: " + id));

        e.setName(dto.getName());
        e.setType(dto.getType());
        e.setCondition(dto.getCondition());
        e.setStatus(dto.getStatus());
        e.setDescription(dto.getDescription());
        e.setProcessId(dto.getProcessId());
        e.setSourceActivityId(dto.getSourceActivityId());
        e.setTargetActivityId(dto.getTargetActivityId());

        return toDTO(repo.save(e));
    }

    @Transactional(readOnly = true)
    public GatewayDTO getById(Long id) {
        return repo.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Gateway no encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public List<GatewayDTO> list() {
        return repo.findAll().stream().map(this::toDTO).toList();
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Gateway no encontrado: " + id);
        }
        repo.deleteById(id);
    }
}
