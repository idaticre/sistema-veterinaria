package com.vet.manadawoof.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(
                name = "ColaboradorEntity.registrarColaborador",
                procedureName = "registrar_colaborador",
                parameters = {
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_id_tipo_persona_juridica", type = Long.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_nombre", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_sexo", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_documento", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_id_tipo_documento", type = Long.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_correo", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_telefono", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_direccion", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_ciudad", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_distrito", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_fecha_ingreso", type = Date.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_id_usuario", type = Long.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_foto", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.OUT, name = "p_codigo_entidad", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.OUT, name = "p_codigo_colaborador", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.OUT, name = "p_mensaje", type = String.class)
                }
        ),
        @NamedStoredProcedureQuery(
                name = "ColaboradorEntity.actualizarColaborador",
                procedureName = "actualizar_colaborador",
                parameters = {
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_id_entidad", type = Long.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_id_tipo_persona_juridica", type = Long.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_nombre", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_sexo", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_documento", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_id_tipo_documento", type = Long.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_correo", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_telefono", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_direccion", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_ciudad", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_distrito", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_fecha_ingreso", type = Date.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_id_usuario", type = Long.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_foto", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_activo", type = Boolean.class),
                        @StoredProcedureParameter(mode = ParameterMode.OUT, name = "p_mensaje", type = String.class)
                }
        )
})
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "colaboradores")
public class ColaboradorEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigo;

    @Temporal(TemporalType.DATE)
    private Date fechaIngreso;

    private String foto;

    private Boolean activo;

    @ManyToOne
    @JoinColumn(name = "id_entidad")
    private EntidadEntity entidad;

    @OneToMany(mappedBy = "colaborador", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<VeterinarioEntity> veterinarios;
}
