package com.web.proyecto.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "activities")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // Relación con Process (tu entidad ya existe)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "process_id", nullable = false)
    private Process process;

        @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_activity_rol"))
    private Rol rol;
}
