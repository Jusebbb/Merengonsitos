package com.web.proyecto.services;

import com.web.proyecto.dtos.UsuarioDTO;
import com.web.proyecto.entities.Empresa;
import com.web.proyecto.entities.Usuario;
import com.web.proyecto.repositories.EmpresaRepository;
import com.web.proyecto.repositories.UsuarioRepository;
import com.web.proyecto.security.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepo;
    private final EmpresaRepository empresaRepo;
    private final PasswordEncoder passwordEncoder;

    private UsuarioDTO toDTO(Usuario u) {
        return UsuarioDTO.builder()
                .id(u.getId())
                .nombre(u.getNombre())
                .email(u.getEmail())
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

    private Long getEmpresaIdFromAuthenticatedUser() {
        UsuarioPrincipal principal = (UsuarioPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getEmpresaId();
    }

    public UsuarioDTO create(UsuarioDTO dto) {
        if (usuarioRepo.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con email: " + dto.getEmail());
        }
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
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
        
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            u.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        u.setEmpresa(empresaRepo.findById(dto.getEmpresaId())
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada: " + dto.getEmpresaId())));

        return toDTO(usuarioRepo.save(u));
    }

    @Transactional(readOnly = true)
    public UsuarioDTO getById(Long id) {
        Long empresaId = getEmpresaIdFromAuthenticatedUser();

        Usuario usuario = usuarioRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id));

        if (!usuario.getEmpresa().getId().equals(empresaId)) {
            throw new IllegalArgumentException("No tiene permisos para ver este usuario.");
        }
        
        return toDTO(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> list() {
        Long empresaId = getEmpresaIdFromAuthenticatedUser();
        return usuarioRepo.findByEmpresaId(empresaId).stream().map(this::toDTO).toList();
    }

    public void delete(Long id) {
        Long empresaId = getEmpresaIdFromAuthenticatedUser();
        
        Usuario usuario = usuarioRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id));
        
        if (!usuario.getEmpresa().getId().equals(empresaId)) {
            throw new IllegalArgumentException("No tiene permisos para eliminar este usuario.");
        }

        usuarioRepo.deleteById(id);
    }
}