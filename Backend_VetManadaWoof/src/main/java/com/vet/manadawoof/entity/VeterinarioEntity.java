package com.vet.manadawoof.entity;

import jakarta.persistence.*;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "veterinarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(
                name = "registrar_veterinario",
                procedureName = "registrar_veterinario",
                parameters = {
                        @StoredProcedureParameter(mode = ParameterMode.IN,  name = "p_nombres",       type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN,  name = "p_apellido_paterno", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN,  name = "p_apellido_materno", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN,  name = "p_direccion",     type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN,  name = "p_telefono",      type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN,  name = "p_correo",        type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN,  name = "p_id_especialidad", type = Integer.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN,  name = "p_cmp",           type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.OUT, name = "p_resultado",     type = String.class)
                }
        ),
        @NamedStoredProcedureQuery(
                name = "actualizar_veterinario",
                procedureName = "actualizar_veterinario",
                parameters = {
                        @StoredProcedureParameter(mode = ParameterMode.IN,  name = "p_id_veterinario", type = Integer.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN,  name = "p_nombres",        type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN,  name = "p_apellido_paterno", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN,  name = "p_apellido_materno", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN,  name = "p_direccion",      type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN,  name = "p_telefono",       type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN,  name = "p_correo",         type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN,  name = "p_id_especialidad", type = Integer.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN,  name = "p_cmp",            type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN,  name = "p_activo",         type = Integer.class),
                        @StoredProcedureParameter(mode = ParameterMode.OUT, name = "p_resultado",      type = String.class)
                }
        )
})
public class VeterinarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "codigo", length = 20, nullable = false, unique = true)
    private String codigo;

    @Column(name = "cmp", length = 32, nullable = false)
    private String cmp;

    @Column(name = "activo", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean activo;

    // Relaciones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_colaborador", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ColaboradorEntity colaborador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_especialidad", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private EspecialidadEntity especialidad;
}
