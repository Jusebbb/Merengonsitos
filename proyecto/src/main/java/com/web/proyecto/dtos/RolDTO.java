package com.web.proyecto.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RolDTO {

    private Long id;

    @NotBlank(message = "El nombre del rol es obligatorio")
    @Size(max = 80, message = "El nombre del rol no puede exceder 80 caracteres")
    private String nombre;

    @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
    private String descripcion;

    private Long empresaId;

    private Long usageCount;
}