package com.web.proyecto.dtos;

import jakarta.validation.constraints.Size;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class EdgeDTO {
    private Long id;
    private Long processId;
    private Long sourceId;
    private Long targetId;

    @Size(max = 45, message = "description máx 45")
    private String description;

    // "ACTIVE" | "INACTIVE" (si viene null, el @PrePersist pone ACTIVE)
    private String status;
}

