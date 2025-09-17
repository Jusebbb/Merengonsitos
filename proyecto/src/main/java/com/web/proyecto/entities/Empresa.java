package com.web.proyecto.entities;

import jakarta.persistence.*;
import lombok.*;
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

    // Relación con usuarios
    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Usuario> usuarios;
}


