package com.vet.manadawoof.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios_roles",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"id_usuario", "id_rol"}, name = "uq_usuario_rol")})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioRolEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    @JsonIgnore  // Evita ciclos al serializar Usuario → UsuarioRol → Usuario
    private UsuarioEntity usuario;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol", nullable = false)
    private RolEntity rol;
    
    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDateTime fechaAsignacion;
    
    @PrePersist
    public void prePersist() {
        if(fechaAsignacion == null) {
            fechaAsignacion = LocalDateTime.now();
        }
    }
}
