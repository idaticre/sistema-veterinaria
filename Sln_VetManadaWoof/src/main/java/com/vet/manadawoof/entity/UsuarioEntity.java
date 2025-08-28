package com.vet.manadawoof.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "usuarios")
@NamedStoredProcedureQuery(
        name = "UsuarioEntity.sp_usuarios",
        procedureName = "sp_usuarios",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN,  name = "p_accion",  type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN,  name = "p_id",      type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN,  name = "p_usuario", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN,  name = "p_clave",   type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN,  name = "p_activo",  type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "p_mensaje", type = String.class)
        }
)
public class UsuarioEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", unique = true)
    private String codigo;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "activo")
    private Boolean activo;
}
