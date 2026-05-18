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
    
    //private int medioPagoID;
    
    private Integer tipoMonedaId;

    private BigDecimal totalGravada;

    private BigDecimal totalInafecta;

    private BigDecimal totalExonerada;

    private BigDecimal totalIGV;

    private BigDecimal total;

    private List<ComprobanteDetalleRequestDTO> detalles;
    
    //private String observaciones;
    
}
