package com.web.proyecto.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessDTO {

    private Long id;

    @NotBlank
    private String name;

    private String description;

    private String category;

    private String status;

    private Long empresaId;
}
