package com.web.proyecto.repositories;

import com.web.proyecto.entities.Activity;
import com.web.proyecto.entities.Status;   // NUEVO
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    // dentro de un proceso concreto
    boolean existsByNameIgnoreCaseAndProcess_Id(String name, Long processId);
    List<Activity> findByProcess_Id(Long processId);
    boolean existsByIdAndProcess_Id(Long id, Long processId);

    // paralelos a Process
    List<Activity> findByProcess_EmpresaId(Long empresaId);
    List<Activity> findByProcess_Status(String status);
    List<Activity> findByProcess_Category(String category);

    // combinado
    List<Activity> findByProcess_EmpresaIdAndProcess_Status(Long empresaId, String status);

    // NUEVO: listar todas menos las eliminadas
    List<Activity> findByStatusNot(String status);
}
