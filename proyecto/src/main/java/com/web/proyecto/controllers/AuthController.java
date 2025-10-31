package com.web.proyecto.controllers;

import com.web.proyecto.dtos.LoginRequest;
import com.web.proyecto.dtos.LoginResponse;
import com.web.proyecto.entities.RolUsuario;
import com.web.proyecto.entities.Usuario;
import com.web.proyecto.repositories.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;

    public AuthController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest body) {
        if (body.getEmail() == null || body.getPassword() == null ||
            body.getEmail().isBlank() || body.getPassword().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Email y contraseña son obligatorios");
        }
        if (body.getPassword().length() < 6) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales inválidas");
        }

        final String rawEmail = body.getEmail();
        final String emailLower = rawEmail.toLowerCase();

        // Busca el usuario por email (tal cual); si no lo encuentra, intenta con lower-case
        Optional<Usuario> maybeUser = usuarioRepository.findByEmail(rawEmail);
        if (maybeUser.isEmpty()) {
            maybeUser = usuarioRepository.findByEmail(emailLower);
        }

        // Rol por defecto (si no hay usuario en BD)
        RolUsuario role = emailLower.contains("admin") ? RolUsuario.ADMIN
                : emailLower.contains("editor") ? RolUsuario.EDITOR
                : RolUsuario.LECTOR;

        Long empresaId = null;
        if (maybeUser.isPresent()) {
            Usuario u = maybeUser.get();
            // Si quieres que el rol venga de BD, usa la línea de abajo:
            if (u.getRol() != null) role = u.getRol();
            if (u.getEmpresa() != null) empresaId = u.getEmpresa().getId();
        }

        // Token DEMO (en real usa JWT)
        String token = UUID.randomUUID().toString();
        String userId = UUID.nameUUIDFromBytes(emailLower.getBytes()).toString();
        String name = emailLower.split("@")[0];

        LoginResponse res = new LoginResponse(token, role, userId, name, empresaId);
        return ResponseEntity.ok(res);
    }
}
