package com.web.proyecto.services;

import com.web.proyecto.dtos.EmpresaDTO;
import com.web.proyecto.entities.Empresa;
import com.web.proyecto.entities.Rol;
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
    // Alternativa directa:
    // private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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

        Empresa e = repo.save(toEntity(dto));

        if (usuarioRepository.existsByEmail(dto.getCorreoContacto())) {
            throw new IllegalArgumentException("El correo del admin ya está en uso: " + dto.getCorreoContacto());
        }

        Usuario admin = new Usuario();
        admin.setNombre("Administrador " + dto.getNombre());  // ✅ campo obligatorio
        admin.setEmail(dto.getCorreoContacto());
        admin.setPassword(passwordEncoder.encode("ChangeMe123!"));
        admin.setRol("ADMIN");   // como String
        admin.setEmpresa(e);

        usuarioRepository.save(admin);

        return toDTO(e);
    }



    public EmpresaDTO update(Long id, EmpresaDTO dto) {
        Empresa e = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada: " + id));

        if (!e.getNit().equals(dto.getNit()) && repo.existsByNit(dto.getNit())) {
            throw new IllegalArgumentException("Ya existe una empresa con NIT: " + dto.getNit());
        }
        if (!e.getCorreoContacto().equals(dto.getCorreoContacto())
                && repo.existsByCorreoContacto(dto.getCorreoContacto())) {
            throw new IllegalArgumentException(
                "Ya existe una empresa con ese correo de contacto: " + dto.getCorreoContacto());
        }

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
        e.setActive(false);
        repo.save(e);
    }
}
