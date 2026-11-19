package com.vet.manadawoof.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "caja_diaria")
@Getter
@Setter
@NoArgsConstructor
public class CajaDiariaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private LocalDate fecha;

    @Column(name = "saldo_apertura_efectivo", nullable = false, precision = 12, scale = 2)
    private BigDecimal saldoAperturaEfectivo = BigDecimal.ZERO;

    @Column(nullable = false, length = 12)
    private String estado = "ABIERTA";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_apertura", nullable = false)
    private UsuarioEntity usuarioApertura;

    @Column(name = "fecha_apertura", nullable = false)
    private LocalDateTime fechaApertura;
}
