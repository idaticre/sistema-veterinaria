package com.vet.manadawoof.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
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
@Table(name = "historia_clinica_registros")
public class HistoriaClinicaRegistroEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(length = 16, nullable = false, unique = true)
    private String codigo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_historia_clinica", nullable = false)
    private HistoriaClinicaEntity historiaClinica;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agenda", nullable = false, unique = true)
    private AgendaEntity agenda;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_veterinario")
    private VeterinarioEntity veterinario;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_colaborador")
    private ColaboradorEntity colaborador;
    
    // ADMINISTRATIVO
    @Column(name = "fecha_atencion", nullable = false)
    private LocalDate fechaAtencion;
    
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;
    
    @Column(name = "hora_fin")
    private LocalTime horaFin;
    
    @Column(name = "tipo_visita", length = 32, nullable = false)
    private String tipoVisita = "GENERAL";
    
    @Column(name = "total_cita", precision = 10, scale = 2)
    private BigDecimal totalCita = BigDecimal.ZERO;
    
    @Column(name = "abono_total", precision = 10, scale = 2)
    private BigDecimal abonoTotal = BigDecimal.ZERO;
    
    @Column(name = "saldo_pendiente", precision = 10, scale = 2)
    private BigDecimal saldoPendiente = BigDecimal.ZERO;
    
    // CLÍNICO
    @Column(name = "motivo_consulta", length = 256)
    private String motivoConsulta;
    
    @Column(columnDefinition = "TEXT", name = "anamnesis")
    private String anamnesis;
    
    @Column(columnDefinition = "TEXT", name = "examen_fisico")
    private String examenFisico;
    
    @Column(name = "signos_vitales", length = 256)
    private String signosVitales;
    
    @Column(name = "peso_kg", precision = 6, scale = 2)
    private BigDecimal pesoKg;
    
    @Column(name = "temperatura_c", precision = 4, scale = 2)
    private BigDecimal temperaturaC;
    
    @Column(columnDefinition = "TEXT")
    private String diagnostico;
    
    @Column(columnDefinition = "TEXT")
    private String tratamiento;
    
    @Column(name = "proximo_control")
    private LocalDate proximoControl;
    
    // ESTÉTICO
    @Column(name = "estado_pelaje", length = 128)
    private String estadoPelaje;
    
    @Column(name = "condicion_piel", length = 128)
    private String condicionPiel;
    
    @Column(name = "observaciones_grooming", columnDefinition = "TEXT")
    private String observacionesGrooming;
    
    // HOSPEDAJE
    @Column(name = "comportamiento_hospedaje", columnDefinition = "TEXT")
    private String comportamientoHospedaje;
    
    @Column(name = "alimentacion_hospedaje", length = 256)
    private String alimentacionHospedaje;
    
    @Column(name = "actividad_hospedaje", columnDefinition = "TEXT")
    private String actividadHospedaje;
    
    // NOTAS GENERALES
    @Column(columnDefinition = "TEXT")
    private String observaciones;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado", nullable = false)
    private EstadoHistoriaClinicaEntity estado;
    
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;
    
    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }
}
