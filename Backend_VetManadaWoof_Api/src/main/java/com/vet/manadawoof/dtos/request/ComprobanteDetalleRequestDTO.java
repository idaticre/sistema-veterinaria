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
    
    @JsonProperty("tipo_unidad_medida_id")
    private Integer tipoUnidadMedidaId;

    @JsonProperty("item_id")
    private Integer itemId;

    private String descripcion;

    private BigDecimal cantidad;

    @JsonProperty("valor_unitario")
    private BigDecimal valorUnitario;

    @JsonProperty("precio_unitario")
    private BigDecimal precioUnitario;

    private BigDecimal descuento;

    private BigDecimal subtotal;

    @JsonProperty("tipo_igv_id")
    private Integer tipoIgvId;

    private BigDecimal igv;

    @JsonProperty("impuestos_bolsas")
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
