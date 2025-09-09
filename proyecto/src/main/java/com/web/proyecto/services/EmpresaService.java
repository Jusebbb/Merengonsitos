package com.web.proyecto.services;

import com.web.proyecto.dtos.EmpresaDTO;
import com.web.proyecto.entities.Empresa;
import com.web.proyecto.repositories.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmpresaService {

    private final EmpresaRepository repo;

    private EmpresaDTO toDTO(Empresa e) {
        return EmpresaDTO.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .nit(e.getNit())
                .correoContacto(e.getCorreoContacto())
                .build();
    }

    private Empresa toEntity(EmpresaDTO d) {
        return Empresa.builder()
                .id(d.getId())
                .nombre(d.getNombre())
                .nit(d.getNit())
                .correoContacto(d.getCorreoContacto())
                .build();
    }

    public EmpresaDTO create(EmpresaDTO dto) {
        if (repo.existsByNit(dto.getNit())) {
            throw new IllegalArgumentException("Ya existe una empresa con NIT: " + dto.getNit());
        }
        Empresa saved = repo.save(toEntity(dto));
        return toDTO(saved);
    }

    public EmpresaDTO update(Long id, EmpresaDTO dto) {
        Empresa e = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada: " + id));

        if (!e.getNit().equals(dto.getNit()) && repo.existsByNit(dto.getNit())) {
            throw new IllegalArgumentException("Ya existe una empresa con NIT: " + dto.getNit());
        }

        e.setNombre(dto.getNombre());
        e.setNit(dto.getNit());
        e.setCorreoContacto(dto.getCorreoContacto());
        return toDTO(repo.save(e));
    }

    @Transactional(readOnly = true)
    public EmpresaDTO getById(Long id) {
        return repo.findById(id).map(this::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public List<EmpresaDTO> list() {
        return repo.findAll().stream().map(this::toDTO).toList();
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Empresa no encontrada: " + id);
        }
        repo.deleteById(id);
    }
}
