package com.web.proyecto.dtos;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class EmpresaDTO {
    private Long id;
    private String nombre;
    private String nit;
    private String correoContacto;
}
