package com.vet.manadawoof.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "dias_semana")
public class DiaEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(length = 20, nullable = false, unique = true)
    private String nombre;
    
    @Column(nullable = false, unique = true)
    private Integer orden;
    
    @OneToMany(mappedBy = "dia")
    @JsonIgnore
    private List<HorarioBaseRolEntity> horarioBaseRoles;
    
    @OneToMany(mappedBy = "dia")
    @JsonIgnore
    private List<AsignacionHorarioEntity> asignaciones;
}
