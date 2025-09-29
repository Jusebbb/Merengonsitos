package com.web.proyecto.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    // ====== Many-to-One: Empresa (muchos procesos pertenecen a una empresa)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_process_empresa"))
    private Empresa empresa;

    // ====== Many-to-One: Rol (muchos procesos pertenecen a un rol)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_process_rol"))
    private Rol rol;

    @OneToMany(mappedBy = "process", cascade = CascadeType.ALL, orphanRemoval = true)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Activity> activities = new ArrayList<>();

    @OneToMany(mappedBy = "process", cascade = CascadeType.ALL, orphanRemoval = true)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Edge> edge = new ArrayList<>();

    @OneToMany(mappedBy = "process", cascade = CascadeType.ALL, orphanRemoval = true)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Gateway> gateways = new ArrayList<>();

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;
}