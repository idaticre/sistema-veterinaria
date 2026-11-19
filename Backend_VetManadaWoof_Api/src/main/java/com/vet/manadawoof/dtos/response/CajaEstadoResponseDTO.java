package com.vet.manadawoof.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CajaEstadoResponseDTO {
    private LocalDate fecha;
    private boolean abierta;
    private boolean saldoAperturaArrastrado;
    private String estado;
    private BigDecimal saldoAperturaEfectivo;
    private BigDecimal saldoEfectivoEsperado;
    private BigDecimal totalIngresos;
    private BigDecimal totalReposiciones;
    private BigDecimal totalEgresos;
    private List<CajaResumenMedioResponseDTO> resumenPorMedio;
    private List<CajaMovimientoResponseDTO> movimientos;
    private BigDecimal saldoEfectivoContado;
    private BigDecimal diferencia;
    private String observacionesCierre;
    private String usuarioApertura;
    private String usuarioCierre;
    private LocalDateTime fechaApertura;
    private LocalDateTime fechaCierre;
}
