package com.web.proyecto.services;

import com.web.proyecto.dtos.UsuarioDTO;
import com.web.proyecto.entities.Empresa;
import com.web.proyecto.entities.Usuario;
import com.web.proyecto.repositories.EmpresaRepository;
import com.web.proyecto.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepo;
    private final EmpresaRepository empresaRepo;

    private UsuarioDTO toDTO(Usuario u) {
        return UsuarioDTO.builder()
                .id(u.getId())
                .nombre(u.getNombre())
                .email(u.getEmail())
                .password(u.getPassword())
                .empresaId(u.getEmpresa().getId())
                .build();
    }

    private Usuario toEntity(UsuarioDTO dto) {
        Empresa empresa = empresaRepo.findById(dto.getEmpresaId())
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada: " + dto.getEmpresaId()));

        return Usuario.builder()
                .id(dto.getId())
                .nombre(dto.getNombre())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .empresa(empresa)
                .build();
    }

    public UsuarioDTO create(UsuarioDTO dto) {
        if (usuarioRepo.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con email: " + dto.getEmail());
        }
        Usuario saved = usuarioRepo.save(toEntity(dto));
        return toDTO(saved);
    }

    public UsuarioDTO update(Long id, UsuarioDTO dto) {
        Usuario u = usuarioRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id));

        if (!u.getEmail().equals(dto.getEmail()) && usuarioRepo.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con email: " + dto.getEmail());
        }

        u.setNombre(dto.getNombre());
        u.setEmail(dto.getEmail());
        u.setPassword(dto.getPassword());
        u.setEmpresa(empresaRepo.findById(dto.getEmpresaId())
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada: " + dto.getEmpresaId())));

        return toDTO(usuarioRepo.save(u));
    }

    @Transactional(readOnly = true)
    public UsuarioDTO getById(Long id) {
        return usuarioRepo.findById(id).map(this::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> list() {
        return usuarioRepo.findAll().stream().map(this::toDTO).toList();
    }

    public void delete(Long id) {
        if (!usuarioRepo.existsById(id)) {
            throw new IllegalArgumentException("Usuario no encontrado: " + id);
        }
        usuarioRepo.deleteById(id);
    }
}