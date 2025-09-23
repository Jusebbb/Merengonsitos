package com.web.proyecto.dtos;

import com.web.proyecto.entities.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityDTO {

    private Long id;

    @NotBlank(message = "name es obligatorio")
    private String name;

    // ===== HU-08 =====
    private String type;
    private String description;
    private Long roleId;

    private Status status; // se envía/recibe como ACTIVE/INACTIVE/DELETED

    @NotNull(message = "processId es obligatorio")
    private Long processId;
}
