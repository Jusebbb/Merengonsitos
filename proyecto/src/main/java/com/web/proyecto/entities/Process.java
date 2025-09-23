package com.web.proyecto.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "process")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Process {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 45)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(length = 45)
    private String category;

    @Column(nullable = false, length = 20)
    private String status = "ACTIVE";

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id", nullable = false, foreignKey = @ForeignKey(name = "fk_process_rol"))
    private Rol rol;

    // ===== Auditoría ligera (HU-05 simplificado) =====
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;
}
