package com.web.proyecto.services;

import com.web.proyecto.dtos.RolDTO;
import com.web.proyecto.entities.Empresa;
import com.web.proyecto.entities.Rol;
import com.web.proyecto.repositories.ActivityRepository;
import com.web.proyecto.repositories.EmpresaRepository;
import com.web.proyecto.repositories.RolRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RolService {

    private final RolRepository rolRepository;
    private final EmpresaRepository empresaRepository;
    private final ActivityRepository activityRepository; // para HU-19/20

    /* ========= Mappers ========= */

    private RolDTO toDTO(Rol r, Long usageCount) {
        return RolDTO.builder()
                .id(r.getId())
                .nombre(r.getNombre())
                .descripcion(r.getDescripcion())
                .empresaId(r.getEmpresa().getId())
                .usageCount(usageCount)
                .build();
    }

    private Rol toEntity(RolDTO d, Empresa empresa) {
        return Rol.builder()
                .id(d.getId())
                .nombre(d.getNombre())
                .descripcion(d.getDescripcion())
                .empresa(empresa)
                .build();
    }

    /* ========= CRUD ========= */

    public RolDTO create(Long empresaId, @Valid RolDTO dto) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada: " + empresaId));

        if (rolRepository.existsByNombreIgnoreCaseAndEmpresa_Id(dto.getNombre(), empresaId)) {
            throw new IllegalArgumentException("Ya existe un rol con ese nombre en la empresa");
        }

        Rol saved = rolRepository.save(toEntity(dto, empresa));
        long usage = 0L; // recién creado
        return toDTO(saved, usage);
    }

    @Transactional(readOnly = true)
    public List<RolDTO> list(Long empresaId) {
        // HU-20: devolver usageCount por rol
        return rolRepository.findByEmpresa_Id(empresaId).stream().map(r -> toDTO(r, activityRepository.countByRol_Id(r.getId()))).toList();
    }

    @Transactional(readOnly = true)
    public RolDTO getById(Long empresaId, Long id) {
        Rol rol = rolRepository.findByIdAndEmpresa_Id(id, empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + id));
        long usage = activityRepository.countByRol_Id(id);
        return toDTO(rol, usage);
    }

    public RolDTO update(Long empresaId, Long id, @Valid RolDTO dto) {
        Rol rol = rolRepository.findByIdAndEmpresa_Id(id, empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + id));

        if (rolRepository.existsByNombreIgnoreCaseAndEmpresa_IdAndIdNot(dto.getNombre(), empresaId, id)) {
            throw new IllegalArgumentException("Ya existe un rol con ese nombre en la empresa");
        }

        rol.setNombre(dto.getNombre());
        rol.setDescripcion(dto.getDescripcion());
        Rol saved = rolRepository.save(rol);
        long usage = activityRepository.countByRol_Id(saved.getId());
        return toDTO(saved, usage);
    }

    public void delete(Long empresaId, Long id) {
        Rol rol = rolRepository.findByIdAndEmpresa_Id(id, empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + id));

        long usage = activityRepository.countByRol_Id(id);
        if (usage > 0) {
            throw new IllegalArgumentException(
                "No se puede eliminar el rol: está asignado a " + usage + " actividad(es)"
            );
        }

        rolRepository.deleteById(id);
    }
}
