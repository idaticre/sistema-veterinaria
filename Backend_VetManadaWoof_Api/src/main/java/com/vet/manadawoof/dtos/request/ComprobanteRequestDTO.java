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
    
    @JsonProperty("agenda_id")
    private Long agendaID;
    
    @JsonProperty("tipo_comprobante_id")
    private int tipoComprobanteID;
    
    @JsonProperty("fecha_emision")
    private LocalDate fechaEmision;

    @JsonProperty("fecha_vencimiento")
    private LocalDate fechaVencimiento;
    
    //private int medioPagoID;
    
    @JsonProperty("tipo_moneda_id")
    private Integer tipoMonedaId;

    @JsonProperty("total_gravada")
    private BigDecimal totalGravada;

    @JsonProperty("total_inafecta")
    private BigDecimal totalInafecta;

    @JsonProperty("total_exonerada")
    private BigDecimal totalExonerada;

    @JsonProperty("total_igv")
    private BigDecimal totalIGV;

    private BigDecimal total;

    private List<ComprobanteDetalleRequestDTO> detalles;
    
    //private String observaciones;
    
}
