package com.vet.manadawoof.dtos.response;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComprobanteResponseDTO {
    
    private long id;
    private String serie;
    private int numero;
    private String fechaEmision;
    private String fechaVencimiento;
    
    private long clienteId;
    private String nombreCliente;
    private String clienteNumeroDoc;
    private String clienteTipoDoc;
    private String clienteDireccion;
    private String clienteCorreo;
    
    private Integer tipoComprobante;
    private Integer moneda;
    private Integer medioPago;
    
    private BigDecimal totalGravada;
    private BigDecimal totalInafecta;
    private BigDecimal totalExonerada;
    private BigDecimal totalIGV;
    private BigDecimal total;
    
    private String observaciones;

    private String pdfurlNubefact;
    
    //private boolean enviadoSunat;
    //private String estadoSunat;
    
    private List<ComprobanteDetalleResponseDTO> detalles;
    
}
