package com.vet.manadawoof.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "agenda_pagos", indexes = {
        @Index(name = "idx_agendapagos_agenda", columnList = "id_agenda"),
        @Index(name = "idx_agendapagos_mediopago", columnList = "id_medio_pago"),
        @Index(name = "idx_agendapagos_fecha", columnList = "fecha_pago")
})
public class AgendaPagoEntity implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(length = 16, nullable = false, unique = true)
    private String codigo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agenda", nullable = false, foreignKey = @ForeignKey(name = "fk_agendapago_agenda"))
    private AgendaEntity agenda;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_medio_pago", nullable = false, foreignKey = @ForeignKey(name = "fk_agendapago_mediopago"))
    private MedioPagoEntity medioPago;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", foreignKey = @ForeignKey(name = "fk_agendapago_usuario"))
    private UsuarioEntity usuario;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private java.math.BigDecimal monto;
    
    @Column(name = "fecha_pago", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private java.time.LocalDateTime fechaPago;
    
    @Column(length = 128)
    private String observaciones;
    
    @PrePersist
    protected void onCreate() {
        if(fechaPago == null) {
            fechaPago = java.time.LocalDateTime.now();
        }
    }
}
