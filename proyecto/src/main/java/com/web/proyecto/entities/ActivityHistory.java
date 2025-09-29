package com.web.proyecto.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "activity_history")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ActivityHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long activityId; // referencia simple a Activity

    private String fieldName; // "name","type","description","roleId","status","processId"

    @Column(length = 1000)
    private String oldValue;

    @Column(length = 1000)
    private String newValue;
}