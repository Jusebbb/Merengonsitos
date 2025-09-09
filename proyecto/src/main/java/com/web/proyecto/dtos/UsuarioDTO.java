package com.web.proyecto.dtos;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String email;
    private String password;
    private Long empresaId; // referencia a la empresa
}
