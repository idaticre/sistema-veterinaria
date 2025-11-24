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
@Table(name = "medicamento_tipo")
public class TipoMedicamentoEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // Identificador único del tipo de medicamento
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    // Nombre del tipo de medicamento
    @Column(length = 32, nullable = false, unique = true)
    private String nombre;
    
    // Descripción del tipo de medicamento
    @Column(length = 128, nullable = false, unique = true)
    private String descripcion;
    
    // Estado de activación del registro
    @Column(name = "activo", nullable = false, columnDefinition = "TINYINT(1)")
    @JdbcTypeCode(SqlTypes.BIT)
    private Boolean activo = true;
}
