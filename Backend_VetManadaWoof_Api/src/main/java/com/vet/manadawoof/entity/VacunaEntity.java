package com.vet.manadawoof.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "vacunas")
public class VacunaEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // Identificador único de la vacuna
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    // Nombre de la vacuna (único)
    @Column(length = 64, nullable = false, unique = true)
    private String nombre;
    
    // ID de la especie a la que pertenece la vacuna
    @Column(name = "id_especie", nullable = false)
    private Integer idEspecie;
    
    // Descripción detallada de la vacuna
    @Column(length = 128, nullable = false, unique = true)
    private String descripcion;
    
    // Indica si la vacuna está activa en el sistema
    @Column(name = "activo", nullable = false, columnDefinition = "TINYINT(1)")
    @JdbcTypeCode(SqlTypes.BIT)
    private Boolean activo = true;
    
    // Relación con la especie correspondiente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_especie", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonIgnore
    private EspecieEntity especie;
}
