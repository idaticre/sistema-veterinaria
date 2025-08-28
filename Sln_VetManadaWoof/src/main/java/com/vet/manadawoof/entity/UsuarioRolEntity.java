package com.vet.manadawoof.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "UsuarioRolEntity")
@Table(name = "usuarios_roles")
@NamedStoredProcedureQuery(
        name = "UsuarioRolEntity.spUsuariosRoles",
        procedureName = "sp_usuarios_roles",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_accion", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_id", type = Long.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_usuario_id", type = Long.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_rol_id", type = Long.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "p_mensaje", type = String.class)
        }
)
public class UsuarioRolEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private UsuarioEntity usuario;

    @ManyToOne
    @JoinColumn(name = "id_rol")
    private RolEntity rol;
}
