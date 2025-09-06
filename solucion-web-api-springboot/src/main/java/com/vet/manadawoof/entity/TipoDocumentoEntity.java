package com.vet.manadawoof.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "TipoDocumentoEntity")
@Table(name = "tipo_documento")
@NamedStoredProcedureQuery(
        name = "TipoDocumentoEntity.spTipoDocumento",
        procedureName = "sp_tipo_documento",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_accion", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_id", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_descripcion", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_activo", type = Boolean.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "p_mensaje", type = String.class)
        }
)
public class TipoDocumentoEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String codigo;

    @Column(length = 32, nullable = false)
    private String descripcion;

    @Column(name = "activo", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean activo;

    @OneToMany(mappedBy = "tipoDocumento")
    @JsonIgnore
    private List<EntidadEntity> entidades;
}
