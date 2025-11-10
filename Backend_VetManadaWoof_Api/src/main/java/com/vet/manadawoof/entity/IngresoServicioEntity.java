package com.vet.manadawoof.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "ingresos_servicios", indexes = {
        @Index(name = "idx_ingresos_servicios_servicio", columnList = "id_servicio"), @Index(name = "idx_ingresos_servicios_colab", columnList = "id_colaborador"), @Index(name = "idx_ingresos_servicios_vet", columnList = "id_veterinario"), @Index(name = "idx_ingresos_servicios_colab_fecha", columnList = "id_colaborador, fecha_registro")})
public class IngresoServicioEntity implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(length = 16, nullable = false, unique = true)
    private String codigo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agenda", nullable = false, foreignKey = @ForeignKey(name = "fk_ingreso_agenda"))
    private AgendaEntity agenda;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_servicio", nullable = false, foreignKey = @ForeignKey(name = "fk_ingreso_servicio"))
    private ServicioEntity servicio;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_colaborador", foreignKey = @ForeignKey(name = "fk_ingreso_colab"))
    private ColaboradorEntity colaborador;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_veterinario", foreignKey = @ForeignKey(name = "fk_ingreso_vet"))
    private VeterinarioEntity veterinario;
    
    @Column
    private Integer cantidad;
    
    @Column(name = "duracion_min")
    private Integer duracionMin;
    
    @Column(length = 128)
    private String observaciones;
    
    @Column(name = "valor_servicio", nullable = false, precision = 10, scale = 2)
    private java.math.BigDecimal valorServicio;
    
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private java.time.LocalDateTime fechaRegistro;
    
    @PrePersist
    protected void onCreate() {
        fechaRegistro = java.time.LocalDateTime.now();
    }
}
