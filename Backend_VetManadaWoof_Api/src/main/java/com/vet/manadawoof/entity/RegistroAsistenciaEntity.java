package com.vet.manadawoof.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "registro_asistencias",
        uniqueConstraints = @UniqueConstraint(name = "uq_asistencia_colab_fecha", columnNames = {"id_colaborador", "fecha"}))
public class RegistroAsistenciaEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_colaborador", nullable = false)
    private ColaboradorEntity colaborador;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_horario_base")
    private HorarioBaseEntity horarioBase;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_estado_asistencia", nullable = false)
    private EstadoAsistenciaEntity estadoAsistencia;
    
    @Column(nullable = false)
    private LocalDate fecha;
    
    @Column(name = "hora_entrada")
    private LocalTime horaEntrada;
    
    @Column(name = "hora_lunch_inicio")
    private LocalTime horaLunchInicio;
    
    @Column(name = "hora_lunch_fin")
    private LocalTime horaLunchFin;
    
    @Column(name = "hora_salida")
    private LocalTime horaSalida;
    
    @Column(name = "minutos_trabajados")
    private Integer minutosTrabajados;
    
    @Column(name = "minutos_lunch")
    private Integer minutosLunch;
    
    @Column(name = "tardanza_minutos")
    private Integer tardanzaMinutos;
    
    @Column(columnDefinition = "TEXT")
    private String observaciones;
    
    @Column(name = "registro_origen", length = 32)
    private String registroOrigen;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Column(name = "created_by")
    private Long createdBy;
    
    @Column(name = "updated_by")
    private Long updatedBy;
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
