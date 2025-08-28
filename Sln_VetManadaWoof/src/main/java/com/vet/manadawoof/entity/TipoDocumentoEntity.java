package com.vet.manadawoof.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "tipo_documento")
@NamedStoredProcedureQuery(
        name = "TipoDocumentoEntity.sp_tipo_documento",
        procedureName = "sp_tipo_documento",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_accion", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_id", type = Long.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_nombre", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_activo", type = Boolean.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "p_mensaje", type = String.class)
        }
)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TipoDocumentoEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "codigo", length = 16, unique = true)
    private String codigo;

    @Column(name = "nombre", length = 64, nullable = false)
    private String nombre;

    @Column(name = "activo")
    private Boolean activo;

    @OneToMany(mappedBy = "tipoDocumento", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<EntidadEntity> entidades;
}
