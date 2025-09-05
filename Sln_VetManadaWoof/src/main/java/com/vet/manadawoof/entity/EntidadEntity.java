package com.vet.manadawoof.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.util.List;

@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(
                name = "EntidadEntity.spRegistrarEntidadBase",
                procedureName = "registrar_entidad_base",
                parameters = {
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_id_tipo_entidad", type = Integer.class),
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
                        @StoredProcedureParameter(mode = ParameterMode.OUT, name = "p_id_entidad", type = Integer.class),
                        @StoredProcedureParameter(mode = ParameterMode.OUT, name = "p_codigo_entidad", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.OUT, name = "p_mensaje", type = String.class)
                }
        ),
        @NamedStoredProcedureQuery(
                name = "EntidadEntity.spActualizarEntidadBase",
                procedureName = "actualizar_entidad_base",
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
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "EntidadEntity")
@Table(name = "entidades")
public class EntidadEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_tipo_entidad")
    private TipoEntidadEntity tipoEntidad;

    @ManyToOne
    @JoinColumn(name = "id_tipo_persona_juridica")
    private TipoPersonaJuridicaEntity tipoPersonaJuridica;

    @Column(length = 128)
    private String nombre;

    @Column(length = 1)
    private String sexo;

    @Column(length = 20)
    private String documento;

    @ManyToOne
    @JoinColumn(name = "id_tipo_documento")
    private TipoDocumentoEntity tipoDocumento;

    @Column(length = 64)
    private String correo;

    @Column(length = 15)
    private String telefono;

    @Column(length = 128)
    private String direccion;

    @Column(length = 64)
    private String ciudad;

    @Column(length = 64)
    private String distrito;

    @Column(length = 64)
    private String representante;

    @Column(length = 20)
    private String codigo;

    @Column(name = "activo", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean activo;

    @OneToMany(mappedBy = "entidad", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ClienteEntity> clientes;

    @OneToMany(mappedBy = "entidad", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ProveedorEntity> proveedores;

    @OneToMany(mappedBy = "entidad", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ColaboradorEntity> colaboradores;
}
