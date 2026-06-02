package com.vet.manadawoof.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComprobanteRequestDTO {
    
    private Long clienteId;
    
    private Long agendaId;
    
    private int tipoComprobanteId;
    
    private LocalDate fechaEmision;

    private LocalDate fechaVencimiento;

    private Integer tipoMonedaId;

    private BigDecimal totalGravada;

    private BigDecimal totalInafecta;

    private BigDecimal totalExonerada;

    private BigDecimal totalIGV;

    private BigDecimal totalGratuita;

    private BigDecimal totalOtrosCargos;

    private BigDecimal total;

    private Integer tipoPercepcionId;

    private BigDecimal percepcionBaseImponible;

    private BigDecimal totalPercepcion;

    private BigDecimal totalIncluidoPercepcion;

    private String observaciones;

    private String codigoUnico;

    private String condicionesPago;

    private String nubecontTipoVentaCodigo;

    private BigDecimal totalAnticipio;

    private Integer medioPagoId;

    private List<ComprobanteDetalleRequestDTO> detalles;
    
}
