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
@Table(name = "medicamentos")
public class MedicamentoEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    // ID autogenerado
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    // Nombre único
    @Column(length = 64, nullable = false, unique = true)
    private String nombre;
    
    // Tipo de medicamento (FK)
    @Column(name = "id_tipo", nullable = false)
    private Integer idtipo;
    
    // Descripción
    @Column(length = 128, nullable = false, unique = true)
    private String descripcion;
    
    // Estado activo/inactivo
    @Column(name = "activo", nullable = false, columnDefinition = "TINYINT(1)")
    @JdbcTypeCode(SqlTypes.BIT)
    private Boolean activo = true;
    
    // Relación con tipo de medicamento
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonIgnore
    private TipoMedicamentoEntity tipoMedicamento;
}
