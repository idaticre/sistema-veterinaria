package com.vet.manadawoof.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "usuarios_roles",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_usuario", "id_rol"}))
public class UsuarioRolEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "id_usuario")
    private UsuarioEntity usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "id_rol")
    private RolEntity rol;

    @Column(name = "fecha_asignacion", insertable = false, updatable = false)
    private java.sql.Timestamp fechaAsignacion;
}
