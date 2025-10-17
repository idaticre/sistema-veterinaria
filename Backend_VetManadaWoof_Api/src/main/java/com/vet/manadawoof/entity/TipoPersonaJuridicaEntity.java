package com.vet.manadawoof.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "tipo_persona_juridica")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoPersonaJuridicaEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 32, nullable = false, unique = true)
    private String nombre;

    @Column(length = 64)
    private String descripcion;

    @Column(name = "activo", nullable = false, columnDefinition = "TINYINT(1)")
    @JdbcTypeCode(SqlTypes.BIT)
    private Boolean activo;

    @OneToMany(mappedBy = "tipoPersonaJuridica")
    @JsonIgnore
    private List<EntidadEntity> entidades;
}
