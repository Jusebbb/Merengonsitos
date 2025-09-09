package com.web.proyecto.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "edge",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_edge_triplet",
        columnNames = {"process_id", "source_activity_id", "target_activity_id"}
    )
)
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Edge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 45)
    private String description;

    @Column(length = 45, nullable = false)
    private String status; // e.g., ACTIVE / INACTIVE

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_id", nullable = false)
    private Process process;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_activity_id", nullable = false)
    private Activity source;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_activity_id", nullable = false)
    private Activity target;

    @PrePersist
    public void prePersist() {
        if (status == null || status.isBlank()) status = "ACTIVE";
    }
}

