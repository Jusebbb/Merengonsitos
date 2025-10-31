package com.web.proyecto.dtos;

import com.web.proyecto.entities.RolUsuario;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
private String token;
    private RolUsuario role;
    private String userId;
    private String name;
    private Long empresaId;
}