package com.web.proyecto.dtos;

import jakarta.persistence.Column;
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

    private String type;
    private String description;
    @Column(name = "rol_id", nullable = true)
private Long roleId;


    /** String (no enum) */
    private String status;

    @NotNull(message = "processId es obligatorio")
    private Long processId;
}