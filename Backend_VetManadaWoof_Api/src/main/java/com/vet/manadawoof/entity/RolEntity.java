// entity/RolEntity.java
package com.vet.manadawoof.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
public class RolEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false, length = 32)
    private String nombre;

    @Column(length = 128)
    private String descripcion;

    @OneToMany(mappedBy = "rol", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<UsuarioRol> usuarioRoles = new HashSet<>();

    // Constructor, getters y setters
    public RolEntity() {}

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Set<UsuarioRol> getUsuarioRoles() { return usuarioRoles; }
    public void setUsuarioRoles(Set<UsuarioRol> usuarioRoles) { this.usuarioRoles = usuarioRoles; }
}