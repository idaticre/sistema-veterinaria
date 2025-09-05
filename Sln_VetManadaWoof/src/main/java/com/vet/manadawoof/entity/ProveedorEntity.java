package com.vet.manadawoof.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "ProveedorEntity")
@Table(name = "proveedores")
@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(
                name = "ProveedorEntity.registrarProveedor",
                procedureName = "registrar_proveedor",
                parameters = {
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_id_tipo_persona_juridica", type = Integer.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_nombre", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_sexo", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_documento", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_id_tipo_documento", type = Integer.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_correo", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_telefono", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_direccion", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_ciudad", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_distrito", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_representante", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.OUT, name = "p_codigo_entidad", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.OUT, name = "p_codigo_proveedor", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.OUT, name = "p_mensaje", type = String.class)
                }
        ),
        @NamedStoredProcedureQuery(
                name = "ProveedorEntity.actualizarProveedor",
                procedureName = "actualizar_proveedor",
                parameters = {
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_id_entidad", type = Integer.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_id_tipo_persona_juridica", type = Integer.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_nombre", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_sexo", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_documento", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_id_tipo_documento", type = Integer.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_correo", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_telefono", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_direccion", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_ciudad", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_distrito", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_representante", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_activo", type = Boolean.class),
                        @StoredProcedureParameter(mode = ParameterMode.OUT, name = "p_mensaje", type = String.class)
                }
        )
})
public class ProveedorEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "codigo", length = 20, nullable = false, unique = true)
    private String codigo;

    @Column(name = "activo", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean activo;

    @ManyToOne
    @JoinColumn(name = "id_entidad", referencedColumnName = "id")
    @JsonIgnoreProperties({"proveedores"})
    private EntidadEntity entidad;
}
