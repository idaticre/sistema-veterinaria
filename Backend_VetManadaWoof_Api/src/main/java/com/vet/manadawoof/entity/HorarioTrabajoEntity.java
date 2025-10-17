package com.vet.manadawoof.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Entidad que representa los horarios base de trabajo.
 * Contiene la información de nombre, descripción, rango de horas y estado activo.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "horarios_base")
public class HorarioTrabajoEntity implements Serializable {
    
    /**
     * Identificador único del horario
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    /**
     * Nombre del horario (único)
     */
    @Column(unique = true, nullable = false, length = 64)
    private String nombre;
    
    /**
     * Descripción del horario (única)
     */
    @Column(unique = true, nullable = false, length = 128)
    private String descripcion;
    
    /**
     * Hora de inicio del horario
     */
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;
    
    /**
     * Hora de fin del horario
     */
    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;
    
    /**
     * Indica si el horario está activo (true/false)
     */
    @Column(name = "activo", nullable = false, columnDefinition = "TINYINT(1)")
    @JdbcTypeCode(SqlTypes.BIT)
    private Boolean activo;
}
