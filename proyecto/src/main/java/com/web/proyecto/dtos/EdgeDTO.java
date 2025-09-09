package com.web.proyecto.dtos;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class EdgeDTO {
    private Long id;
    private Long processId;
    private Long sourceId;
    private Long targetId;

    private String description; // opcional (<=45)
    private String status;      // opcional: "ACTIVE"/"INACTIVE" (si viene null, se pone ACTIVE en @PrePersist)
}

