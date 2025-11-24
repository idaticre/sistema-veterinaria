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
@Table(name = "etapas_vida")
public class EtapaVidaEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // Identificador único de la etapa de vida
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    // Nombre o descripción de la etapa (ej. cachorro, adulto, senior)
    @Column(length = 16, nullable = false, unique = true)
    private String descripcion;
    
    // Indica si la etapa está activa en el sistema
    @Column(name = "activo", nullable = false, columnDefinition = "TINYINT(1)")
    @JdbcTypeCode(SqlTypes.BIT)
    private Boolean activo = true;
    
}
