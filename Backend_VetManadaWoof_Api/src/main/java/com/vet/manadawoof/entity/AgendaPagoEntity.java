package com.vet.manadawoof.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "agenda_pagos")
public class AgendaPagoEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(length = 16, nullable = false, unique = true)
    private String codigo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agenda", nullable = false)
    private AgendaEntity agenda;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_medio_pago", nullable = false)
    private MedioPagoEntity medioPago;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
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
