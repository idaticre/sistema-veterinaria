package com.vet.manadawoof.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComprobanteDetalleRequestDTO {
    
    private Integer tipoUnidadMedidaId;

    private Integer itemId;

    private String descripcion;

    private BigDecimal cantidad;

    private BigDecimal valorUnitario;

    private BigDecimal precioUnitario;

    private BigDecimal descuento;

    private BigDecimal subtotal;

    private Integer tipoIgvId;

    private BigDecimal igv;

    private BigDecimal impuestosBolsas;

    private BigDecimal total;

    private Boolean anticipoRegularizacion;

    private String anticipoDocumentoSerie;

    private Integer anticipoDocumentoNumero;

    private String codigoProductoSunat;
    
}
