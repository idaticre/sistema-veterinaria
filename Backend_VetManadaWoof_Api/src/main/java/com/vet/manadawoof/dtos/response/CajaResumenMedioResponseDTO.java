package com.vet.manadawoof.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CajaResumenMedioResponseDTO {
    private Integer medioPagoId;
    private String medioPago;
    private BigDecimal ingresos;
    private BigDecimal reposiciones;
    private BigDecimal egresos;
    private BigDecimal neto;
}
