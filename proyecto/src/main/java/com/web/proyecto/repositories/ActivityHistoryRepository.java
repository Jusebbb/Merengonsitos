package com.web.proyecto.repositories;

import com.web.proyecto.entities.ActivityHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityHistoryRepository extends JpaRepository<ActivityHistory, Long> { }
