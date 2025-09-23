package com.web.proyecto.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** DTO para crear/editar procesos */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessDTO {

    private Long id;

    @NotBlank(message = "El nombre del proceso es obligatorio")
    private String name;

    private String description;

    private String category;

    /** "ACTIVE" | "INACTIVE" | "DRAFT" (el Service lo mapea al enum) */
    private String status;

    @NotNull(message = "El id de la empresa es obligatorio")
    private Long empresaId;

    @NotNull(message = "El id del rol es obligatorio")
    private Long rolId;
}
