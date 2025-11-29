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
@Table(name = "historia_clinica_registros", indexes = {
        @Index(name = "idx_registro_historia", columnList = "id_historia_clinica"), @Index(name = "idx_registro_agenda", columnList = "id_agenda"), @Index(name = "idx_registro_fecha", columnList = "fecha_atencion"), @Index(name = "idx_registro_veterinario", columnList = "id_veterinario"), @Index(name = "idx_registro_estado", columnList = "id_estado"), @Index(name = "idx_registro_codigo", columnList = "codigo", unique = true)})
public class HistoriaClinicaRegistroEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(length = 16, nullable = false, unique = true)
    private String codigo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_historia_clinica", nullable = false, foreignKey = @ForeignKey(name = "fk_registro_historia"))
    private HistoriaClinicaEntity historiaClinica;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agenda", foreignKey = @ForeignKey(name = "fk_registro_agenda"))
    private AgendaEntity agenda;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_veterinario", foreignKey = @ForeignKey(name = "fk_registro_veterinario"))
    private VeterinarioEntity veterinario;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_colaborador", foreignKey = @ForeignKey(name = "fk_registro_colaborador"))
    private ColaboradorEntity colaborador;
    
    @Column(name = "fecha_atencion", nullable = false)
    private LocalDate fechaAtencion;
    
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;
    
    @Column(name = "hora_fin")
    private LocalTime horaFin;
    
    @Column(length = 256)
    private String motivoConsulta;
    
    @Column(columnDefinition = "TEXT")
    private String anamnesis;
    
    @Column(columnDefinition = "TEXT")
    private String examenFisico;
    
    @Column(length = 256)
    private String signosVitales;
    
    @Column(name = "peso_kg", precision = 6, scale = 2)
    private BigDecimal pesoKg;
    
    @Column(name = "temperatura_c", precision = 4, scale = 2)
    private BigDecimal temperaturaC;
    
    @Column(columnDefinition = "TEXT")
    private String diagnostico;
    
    @Column(columnDefinition = "TEXT")
    private String tratamiento;
    
    @Column(columnDefinition = "TEXT")
    private String observaciones;
    
    @Column
    private LocalDate proximoControl;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado", foreignKey = @ForeignKey(name = "fk_registro_estado"))
    private EstadoHistoriaClinicaEntity estado;
    
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime fechaRegistro;
    
    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }
}
