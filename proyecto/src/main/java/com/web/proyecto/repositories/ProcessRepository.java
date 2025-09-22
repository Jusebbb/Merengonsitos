package com.web.proyecto.repositories;

import com.web.proyecto.entities.Process;
import com.web.proyecto.entities.ProcessStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProcessRepository extends JpaRepository<Process, Long> {
    List<Process> findByEmpresaId(Long empresaId);
    List<Process> findByStatus(ProcessStatus status);
    List<Process> findByCategory(String category);
    List<Process> findByEmpresaIdAndStatus(Long empresaId, ProcessStatus status);
}
