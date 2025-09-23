package com.web.proyecto.dtos;

import com.web.proyecto.entities.ProcessStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessDTO {
    private Long id;

    @NotBlank
    private String name;

    private String description;

    private String category;

    private ProcessStatus status;

    @NotNull
    private Long empresaId;

    @NotNull
    private Long rolId;
}
