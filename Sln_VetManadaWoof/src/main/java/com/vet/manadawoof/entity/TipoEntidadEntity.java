package com.vet.manadawoof.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "TipoEntidadEntity")
@Table(name = "tipo_entidad")
@NamedStoredProcedureQuery(
        name = "TipoEntidadEntity.spTipoEntidad",
        procedureName = "sp_tipo_entidad",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_accion", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_id", type = Long.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_nombre", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_activo", type = Boolean.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "p_mensaje", type = String.class)
        }
)
public class TipoEntidadEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 16, unique = true)
    private String codigo;

    @Column(length = 64, nullable = false, unique = true)
    private String nombre;

    private Boolean activo;
}
