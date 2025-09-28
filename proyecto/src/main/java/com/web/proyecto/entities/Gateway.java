package com.web.proyecto.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "gateway")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Gateway {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String name;

    @Column(nullable = false, length = 30)
    private String type;   // EXCLUSIVE, PARALLEL, etc.

    @Column(name = "condition_expr", length = 255)
    private String condition; 

    @Column(nullable = false, length = 20)
    private String status;

    @Column(length = 255)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "process_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_gateway_process"))
    private Process process;

    @Column(name = "source_activity_id", nullable = false)
    private Long sourceActivityId;

    @Column(name = "target_activity_id", nullable = false)
    private Long targetActivityId;
}
