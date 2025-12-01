package com.vet.manadawoof.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "agenda")
public class AgendaEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(length = 16, nullable = false, unique = true)
    private String codigo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private ClienteEntity cliente;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mascota", nullable = false)
    private MascotaEntity mascota;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_medio_solicitud")
    private MedioSolicitudEntity medioSolicitud;
    
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private java.time.LocalDate fecha;
    
    @Column(nullable = false)
    private java.time.LocalTime hora;
    
    @Column(name = "duracion_estimada_min")
    private Integer duracionEstimadaMin;
    
    @Column(name = "abono_inicial", precision = 10, scale = 2)
    private java.math.BigDecimal abonoInicial = java.math.BigDecimal.ZERO;
    
    @Column(name = "total_cita", precision = 10, scale = 2)
    private java.math.BigDecimal totalCita = java.math.BigDecimal.ZERO;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado", nullable = false)
    private EstadoAgendaEntity estado;
    
    @Column(length = 256)
    private String observaciones;
    
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private java.time.LocalDateTime fechaRegistro;
    
    @PrePersist
    protected void onCreate() {
        fechaRegistro = java.time.LocalDateTime.now();
    }
    
    @OneToMany(mappedBy = "agenda", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<IngresoServicioEntity> ingresosServicios;
    
    @OneToMany(mappedBy = "agenda", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<AgendaPagoEntity> agendaPagos;
    
    @OneToMany(mappedBy = "agenda", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<RecordatorioAgendaEntity> recordatorios;
}
