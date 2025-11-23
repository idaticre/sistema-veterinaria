package com.vet.manadawoof.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "horarios_base")
public class HorarioBaseEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(unique = true, nullable = false, length = 64)
    private String nombre;
    
    @Column(length = 128)
    private String descripcion;
    
    @Column(name = "hora_inicio")
    private LocalTime horaInicio;
    
    @Column(name = "hora_fin")
    private LocalTime horaFin;
    
    @Column(name = "minutos_tolerancia_entrada")
    private Integer minutoToleranciaEntrada;
    
    @Column(name = "minutos_lunch", nullable = false)
    private Integer minutosLunch = 60;
    
    @Column(name = "overnight", nullable = false, columnDefinition = "TINYINT(1)")
    @JdbcTypeCode(SqlTypes.BIT)
    private Boolean overnight = false;
    
    @Column(name = "activo", nullable = false, columnDefinition = "TINYINT(1)")
    @JdbcTypeCode(SqlTypes.BIT)
    private Boolean activo;
    
    @OneToMany(mappedBy = "horarioBase")
    @JsonIgnore
    private List<HorarioBaseRolEntity> horarioBaseRoles;
    
    @OneToMany(mappedBy = "horarioBase")
    @JsonIgnore
    private List<AsignacionHorarioEntity> asignaciones;
    
    @OneToMany(mappedBy = "horarioBase")
    @JsonIgnore
    private List<RegistroAsistenciaEntity> registrosAsistencia;
}
