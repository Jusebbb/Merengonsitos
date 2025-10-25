package com.web.proyecto.services;

import com.web.proyecto.dtos.EmpresaDTO;
import com.web.proyecto.entities.Empresa;
import com.web.proyecto.entities.Rol;
import com.web.proyecto.entities.RolUsuario;
import com.web.proyecto.entities.Usuario;
import com.web.proyecto.repositories.EmpresaRepository;
import com.web.proyecto.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmpresaService {

    private final EmpresaRepository repo;
    private final UsuarioRepository usuarioRepository;

    // Encoder local (sin @Bean ni otra clase)
    private final PasswordEncoder passwordEncoder =
            PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private EmpresaDTO toDTO(Empresa e) {
        return EmpresaDTO.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .nit(e.getNit())
                .correoContacto(e.getCorreoContacto())
                .password(e.getPassword())
                .build();
    }

    private Empresa toEntity(EmpresaDTO d) {
        return Empresa.builder()
                .id(d.getId())
                .nombre(d.getNombre())
                .nit(d.getNit())
                .correoContacto(d.getCorreoContacto())
                .password(d.getPassword())
                .active(true)
                .build();
    }

    public EmpresaDTO create(EmpresaDTO dto) {
    if (repo.existsByNit(dto.getNit())) {
        throw new IllegalArgumentException("Ya existe una empresa con NIT: " + dto.getNit());
    }
    if (repo.existsByCorreoContacto(dto.getCorreoContacto())) {
        throw new IllegalArgumentException("Ya existe una empresa con ese correo de contacto: " + dto.getCorreoContacto());
    }

    // Si la contraseña de la empresa no es proporcionada, puedes lanzar un error o asignar un valor predeterminado
    if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
        throw new IllegalArgumentException("La contraseña de la empresa es obligatoria.");
    }

    // Crear la empresa
    Empresa e = repo.save(toEntity(dto));

    // Verificar si el correo del administrador ya existe
    if (usuarioRepository.existsByEmail(dto.getCorreoContacto())) {
        throw new IllegalArgumentException("El correo del admin ya está en uso: " + dto.getCorreoContacto());
    }

    // Crear el usuario administrador
    Usuario admin = new Usuario();
    admin.setNombre("Administrador " + dto.getNombre());  // Nombre del admin
    admin.setEmail(dto.getCorreoContacto());
    admin.setPassword(passwordEncoder.encode(dto.getPassword()));  // Codificar la contraseña
    admin.setRol(RolUsuario.ADMIN);  // Asignar el rol ADMIN
    admin.setEmpresa(e);  // Asociar el admin a la empresa

    usuarioRepository.save(admin);

    return toDTO(e);
}


    public EmpresaDTO update(Long id, EmpresaDTO dto) {
        Empresa e = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada: " + id));

        // Verificar que no haya duplicados en NIT o correo de contacto
        if (!e.getNit().equals(dto.getNit()) && repo.existsByNit(dto.getNit())) {
            throw new IllegalArgumentException("Ya existe una empresa con NIT: " + dto.getNit());
        }
        if (!e.getCorreoContacto().equals(dto.getCorreoContacto())
                && repo.existsByCorreoContacto(dto.getCorreoContacto())) {
            throw new IllegalArgumentException(
                "Ya existe una empresa con ese correo de contacto: " + dto.getCorreoContacto());
        }

        // Actualizar los detalles de la empresa
        e.setNombre(dto.getNombre());
        e.setNit(dto.getNit());
        e.setCorreoContacto(dto.getCorreoContacto());
        return toDTO(repo.save(e));
    }

    @Transactional(readOnly = true)
    public EmpresaDTO getById(Long id) {
        Empresa e = repo.findById(id)
                .filter(Empresa::isActive)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada o inactiva: " + id));
        return toDTO(e);
    }

    @Transactional(readOnly = true)
    public List<EmpresaDTO> list() {
        return repo.findAll().stream()
                .filter(Empresa::isActive)
                .map(this::toDTO)
                .toList();
    }

    public void delete(Long id) {
        Empresa e = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada: " + id));
        e.setActive(false);  // Realizar un "soft-delete" cambiando el estado de la empresa a inactiva
        repo.save(e);
    }
}
