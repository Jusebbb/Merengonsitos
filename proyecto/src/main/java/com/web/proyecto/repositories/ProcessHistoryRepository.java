package com.web.proyecto.repositories;

import com.web.proyecto.entities.ProcessHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessHistoryRepository extends JpaRepository<ProcessHistory, Long> {
}