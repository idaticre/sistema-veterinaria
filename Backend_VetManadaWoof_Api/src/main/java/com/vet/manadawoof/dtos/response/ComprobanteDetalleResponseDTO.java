package com.vet.manadawoof.dtos.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComprobanteDetalleResponseDTO {
    
    private long Id;
    
    private int itemId;
    
    private String descripcion;
    
    private BigDecimal cantidad;
    
    private BigDecimal valorUnitario;
    
    private BigDecimal precioUnitario;
    
    private BigDecimal descuento;
    
    private BigDecimal subtotal;
    
    private BigDecimal igv;
    
    private BigDecimal total;
    
    private String unidadMedida;
    
    private String tipoIGV;
    
}
