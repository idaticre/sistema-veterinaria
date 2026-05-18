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

    //@JsonProperty("anticipio_regularizacion")
    //private Boolean anticipoRegularizacion;

    //@JsonProperty("anticipio_documento_serie")
    //private String anticipoDocumentoSerie;

    //@JsonProperty("anticipio_documento_numero")
    //private Integer anticipoDocumentoNumero;

    //@JsonProperty("codigo_producto_sunat")
    //private String codigoProductoSunat;
    
}
