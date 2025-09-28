package com.web.proyecto.repositories;

import com.web.proyecto.entities.Edge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EdgeRepository extends JpaRepository<Edge, Long> {
    java.util.List<com.web.proyecto.entities.Edge> findByProcess_Id(Long processId);

    boolean existsByProcess_IdAndSource_IdAndTarget_Id(Long processId, Long sourceId, Long targetId);
}

