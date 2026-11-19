package com.vet.manadawoof.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "caja_cierres")
@Getter
@Setter
@NoArgsConstructor
public class CajaCierreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caja_diaria_id", nullable = false, unique = true)
    private CajaDiariaEntity cajaDiaria;

    @Column(name = "saldo_efectivo_esperado", nullable = false, precision = 12, scale = 2)
    private BigDecimal saldoEfectivoEsperado;

    @Column(name = "saldo_efectivo_contado", nullable = false, precision = 12, scale = 2)
    private BigDecimal saldoEfectivoContado;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal diferencia;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_cierre", nullable = false)
    private UsuarioEntity usuarioCierre;

    @Column(name = "fecha_cierre", nullable = false)
    private LocalDateTime fechaCierre;
}
