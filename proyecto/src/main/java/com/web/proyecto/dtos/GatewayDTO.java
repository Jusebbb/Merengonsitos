package com.web.proyecto.dtos;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GatewayDTO {

    private Long id;
    private String name;
    private String type;
    private String condition;
    private String status;
    private String description;
    private Long processId;
    private Long sourceActivityId;
    private Long targetActivityId;
}
