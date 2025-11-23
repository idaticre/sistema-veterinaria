package com.vet.manadawoof.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "horarios_base_roles",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_rol_horario_dia",
                        columnNames = {"id_rol", "id_horario_base", "id_dia_semana"})})
public class HorarioBaseRolEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rol", nullable = false)
    @JsonIgnore
    private RolEntity rol;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_horario_base", nullable = false)
    @JsonIgnore
    private HorarioBaseEntity horarioBase;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dia_semana", nullable = false)
    @JsonIgnore
    private DiaEntity dia;
}
