package com.web.proyecto.controllers;

import com.web.proyecto.dtos.LoginRequest;
import com.web.proyecto.dtos.LoginResponse;
import com.web.proyecto.entities.RolUsuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200") // permite al front dev
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest body) {
        // ======= VALIDACIONES BÁSICAS =======
        if (body.getEmail() == null || body.getPassword() == null ||
            body.getEmail().isBlank() || body.getPassword().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Email y contraseña son obligatorios");
        }
        if (body.getPassword().length() < 6) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales inválidas");
        }

        // ======= LÓGICA DEMO (reemplaza por DB más adelante) =======
        String email = body.getEmail().toLowerCase();
        RolUsuario role;

        if (email.contains("admin")) {
            role = RolUsuario.ADMIN;
        } else if (email.contains("editor")) {
            role = RolUsuario.EDITOR;
        } else {
            role = RolUsuario.LECTOR;
        }

        // Genera token simple (en real usa JWT)
        String token = UUID.randomUUID().toString();
        String userId = UUID.nameUUIDFromBytes(email.getBytes()).toString();
        String name = email.split("@")[0];

        LoginResponse res = new LoginResponse(token, role, userId, name);
        return ResponseEntity.ok(res);
    }
}
