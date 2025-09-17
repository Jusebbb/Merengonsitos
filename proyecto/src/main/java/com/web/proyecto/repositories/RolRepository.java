package com.web.proyecto.repositories;

import com.web.proyecto.entities.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {

    boolean existsByNombreIgnoreCaseAndEmpresa_Id(String nombre, Long empresaId);

    boolean existsByNombreIgnoreCaseAndEmpresa_IdAndIdNot(String nombre, Long empresaId, Long excludeId);

    List<Rol> findByEmpresa_Id(Long empresaId);

    Optional<Rol> findByIdAndEmpresa_Id(Long id, Long empresaId);
    Optional<Rol> findByNombreIgnoreCaseAndEmpresa_Id(String nombre, Long empresaId);
}
