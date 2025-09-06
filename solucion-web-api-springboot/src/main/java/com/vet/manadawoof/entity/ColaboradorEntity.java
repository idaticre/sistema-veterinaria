package com.vet.manadawoof.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(
                name = "ColaboradorEntity.spRegistrarColaborador",
                procedureName = "registrar_colaborador",
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
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_fecha_ingreso", type = LocalDate.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_id_usuario", type = Integer.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_foto", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.OUT, name = "p_codigo_entidad", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.OUT, name = "p_codigo_colaborador", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.OUT, name = "p_mensaje", type = String.class)
                }
        ),
        @NamedStoredProcedureQuery(
                name = "ColaboradorEntity.spActualizarColaborador",
                procedureName = "actualizar_colaborador",
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
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_fecha_ingreso", type = LocalDate.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_id_usuario", type = Integer.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_foto", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_activo", type = Boolean.class),
                        @StoredProcedureParameter(mode = ParameterMode.OUT, name = "p_mensaje", type = String.class)
                }
        )
})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "colaboradores")
public class ColaboradorEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_entidad", nullable = false)
    @JsonIgnore
    private EntidadEntity entidad;

    @OneToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private UsuarioEntity usuario;

    @Column(length = 20, nullable = false)
    private String codigo;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    @Column(length = 255)
    private String foto;

    @Column(name = "activo", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean activo;
}
