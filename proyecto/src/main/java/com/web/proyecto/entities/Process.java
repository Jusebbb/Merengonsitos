package com.web.proyecto.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.UpdateTimestamp;

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

    // 🔹 Este sí debes setearlo en el Service con el usuario que edita
    @Column(name = "updated_by", length = 100)
    private String updatedBy;
}
