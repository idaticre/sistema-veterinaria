package com.vet.manadawoof.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(
                name = "VeterinarioEntity.registrarVeterinario",
                procedureName = "registrar_veterinario",
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
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_representante", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_id_especialidad", type = Long.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_cmp", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.OUT, name = "p_codigo_entidad", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.OUT, name = "p_codigo_colaborador", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.OUT, name = "p_codigo_veterinario", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.OUT, name = "p_mensaje", type = String.class)
                }
        ),
        @NamedStoredProcedureQuery(
                name = "VeterinarioEntity.actualizarVeterinario",
                procedureName = "actualizar_veterinario",
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
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_representante", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_id_usuario", type = Long.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_foto", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_id_especialidad", type = Long.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_cmp", type = String.class),
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
@Table(name = "veterinarios")
public class VeterinarioEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigo;

    private String cmp;

    private Boolean activo;

    @ManyToOne
    @JoinColumn(name = "id_colaborador")
    private ColaboradorEntity colaborador;

    @ManyToOne
    @JoinColumn(name = "id_especialidad")
    private EspecialidadEntity especialidad;
}
