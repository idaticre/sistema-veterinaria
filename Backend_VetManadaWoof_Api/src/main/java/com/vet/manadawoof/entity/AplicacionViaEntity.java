package com.vet.manadawoof.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;

/**
 * Entidad que representa las vías de aplicación de medicamentos.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "vias_aplicacion")
public class AplicacionViaEntity implements Serializable {
    
    // Identificador único de la vía de aplicación
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    // Nombre de la vía de aplicación
    @Column(length = 32, nullable = false, unique = true)
    private String nombre;
    
    // Estado del registro (activo/inactivo)
    @Column(name = "activo", nullable = false, columnDefinition = "TINYINT(1)")
    @JdbcTypeCode(SqlTypes.BIT)
    private Boolean activo = true;
}
