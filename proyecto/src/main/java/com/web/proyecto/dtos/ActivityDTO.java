package com.web.proyecto.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ActivityDTO {

    private Long id;

    @NotBlank(message = "name es obligatorio")
    private String name;

    @NotNull(message = "processId es obligatorio")
    private Long processId;
}
