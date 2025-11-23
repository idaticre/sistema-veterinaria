package com.vet.manadawoof.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "estado_mascota")
public class EstadoMascotaEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    // ID autogenerado
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    // Nombre único
    @Column(length = 32, nullable = false, unique = true)
    private String nombre;
    
    // Descripción
    @Column(length = 128, nullable = false, unique = true)
    private String descripcion;
    
    // Estado activo/inactivo
    @Column(name = "activo", nullable = false, columnDefinition = "TINYINT(1)")
    @JdbcTypeCode(SqlTypes.BIT)
    private Boolean activo = true;
}
