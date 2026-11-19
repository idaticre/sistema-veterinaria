package com.vet.manadawoof.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CajaMovimientoResponseDTO {
    private Long id;
    private String tipo;
    private String concepto;
    private BigDecimal monto;
    private Integer medioPagoId;
    private String medioPago;
    private String usuario;
    private String cliente;
    private String asociado;
    private String producto;
    private Integer cantidadProducto;
    private String servicio;
    private String origen;
    private LocalDateTime fechaHora;
    private String comprobanteNombre;
    private boolean tieneComprobante;
}
