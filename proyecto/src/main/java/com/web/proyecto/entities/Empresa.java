package com.web.proyecto.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "empresa", uniqueConstraints = {
        @UniqueConstraint(name = "empresa_nit", columnNames = "nit")
})
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, unique = true, length = 30)
    private String nit;

    @Column(nullable = false, length = 120)
    private String correoContacto;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private String password;  // Campo para la contraseña de la empresa

    // Relación con usuarios (como ya la tenías)
    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Usuario> usuarios = new ArrayList<>();

    // ====== One-to-Many: una empresa tiene muchos procesos
    @OneToMany(mappedBy = "empresa") // sin orphanRemoval para no borrar en cascada
    private List<Process> processes = new ArrayList<>();

    // (opcional, consistente) una empresa tiene muchos roles
    @OneToMany(mappedBy = "empresa")
    private List<Rol> roles = new ArrayList<>();
}
