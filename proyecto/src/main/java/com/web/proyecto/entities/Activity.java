package com.web.proyecto.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "activities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // ===== HU-08: nuevos campos =====
    private String type;

    @Column(length = 1000)
    private String description;

    private Long roleId;

    @Column(nullable = false, length = 20)
    private String status = "ACTIVE";

    // Relación ya existente con Process
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "process_id", nullable = false)
    private Process process;
}
