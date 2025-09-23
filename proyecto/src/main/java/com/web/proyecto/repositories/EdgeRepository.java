package com.web.proyecto.repositories;

import com.web.proyecto.entities.Edge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EdgeRepository extends JpaRepository<Edge, Long> {
    List<Edge> findByProcess_Id(Long processId);

    boolean existsByProcess_IdAndSource_IdAndTarget_Id(Long processId, Long sourceId, Long targetId);

    long countBySource_Id(Long sourceId);
    long countByTarget_Id(Long targetId);

    List<Edge> findBySource_Id(Long sourceId);
    List<Edge> findByTarget_Id(Long targetId);
}

