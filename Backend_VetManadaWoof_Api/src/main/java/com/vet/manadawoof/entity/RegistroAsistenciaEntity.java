package com.vet.manadawoof.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.*;

@Entity
@Table(
        name = "registro_asistencias",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_asistencia_colab_fecha",
                        columnNames = {"id_colaborador", "fecha"}
                )
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroAsistenciaEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // Identificador único del registro de asistencia
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Colaborador al que pertenece el registro de asistencia
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_colaborador", nullable = false)
    private ColaboradorEntity colaborador;
    
    // Horario base de referencia (opcional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_horario_base")
    private HorarioTrabajoEntity horarioBase;
    
    // Fecha correspondiente a la asistencia (única por colaborador)
    @Column(nullable = false)
    private LocalDate fecha;
    
    // Hora exacta en la que se registró la entrada
    @Column(name = "hora_entrada")
    private LocalTime horaEntrada;
    
    // Hora de inicio del almuerzo
    @Column(name = "hora_lunch_inicio")
    private LocalTime horaLunchInicio;
    
    // Hora de finalización del almuerzo
    @Column(name = "hora_lunch_fin")
    private LocalTime horaLunchFin;
    
    // Hora en que el colaborador registró su salida
    @Column(name = "hora_salida")
    private LocalTime horaSalida;
    
    // Total de minutos trabajados en la jornada
    @Column(name = "minutos_trabajados")
    private Integer minutosTrabajados;
    
    // Duración total del almuerzo en minutos
    @Column(name = "minutos_lunch")
    private Integer minutosLunch;
    
    // Minutos de tardanza respecto al horario base
    @Column(name = "tardanza_minutos")
    private Integer tardanzaMinutos;
    
    // Estado actual del registro (PENDIENTE, PRESENTE, ALMUERZO, COMPLETADO)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado_asistencia", nullable = false)
    private EstadoAsistenciaEntity estadoAsistencia;
    
    // Comentarios o notas adicionales del registro
    @Column(columnDefinition = "TEXT")
    private String observaciones;
    
    // Origen del registro: APP, BIOMETRICO o MANUAL
    @Column(name = "registro_origen", length = 32)
    private String registroOrigen;
    
    // Fecha y hora en que se creó el registro
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    // Fecha y hora de la última actualización
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // Usuario que creó el registro
    @Column(name = "created_by")
    private Long createdBy;
    
    // Usuario que actualizó el registro por última vez
    @Column(name = "updated_by")
    private Long updatedBy;
}
