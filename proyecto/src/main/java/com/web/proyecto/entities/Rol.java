package com.web.proyecto.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "rol",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_rol_nombre_empresa", columnNames = {"nombre", "empresa_id"})
    }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_rol_empresa"))
    private Empresa empresa;

    // ====== One-to-Many: un rol tiene muchos procesos
    @OneToMany(mappedBy = "rol") // sin cascade remove para no borrar procesos al borrar rol
    private List<Process> processes = new ArrayList<>();
}