package com.web.proyecto.repositories;

import com.web.proyecto.entities.Process;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface ProcessRepository extends JpaRepository<Process, Long> {
    List<Process> findByEmpresaId(Long empresaId);
    List<Process> findByStatus(String status);
    List<Process> findByEmpresaIdAndStatus(Long empresaId, String status);
    List<Process> findByNameContainingIgnoreCase(String name);
    long countByRol_Id(Long rolId);
}
