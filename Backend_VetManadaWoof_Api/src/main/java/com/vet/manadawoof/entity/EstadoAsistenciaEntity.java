package com.vet.manadawoof.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

/**
 * Catálogo de estados posibles de una asistencia.
 * Ejemplo: PENDIENTE, PRESENTE, ALMUERZO, COMPLETADO, INASISTENTE.
 */
@Entity
@Table(name = "estado_asistencia")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoAsistenciaEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // Identificador único del estado
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    // Nombre corto del estado (ej. PRESENTE, COMPLETADO)
    @Column(nullable = false, unique = true, length = 50)
    private String nombre;
    
    // Descripción más detallada del estado (opcional)
    @Column(length = 255)
    private String descripcion;
    
}
