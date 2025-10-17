package com.vet.manadawoof.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "asignacion_horarios",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"id_colaborador", "id_dia_semana"})})
public class AsignacionHorarioEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Relación con colaborador
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_colaborador", nullable = false)
    @JsonIgnore
    private ColaboradorEntity colaborador;
    
    // Relación con horario base
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_horario_base", nullable = false)
    @JsonIgnore
    private HorarioTrabajoEntity horarioBase;
    
    // Relación con día de la semana
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dia_semana", nullable = false)
    @JsonIgnore
    private DiaEntity dia;
    
    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDateTime fechaAsignacion;
    
    @Column(name = "activo", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean activo;
}
