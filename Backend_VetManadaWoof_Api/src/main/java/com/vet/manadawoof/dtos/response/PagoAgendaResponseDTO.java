package com.vet.manadawoof.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PagoAgendaResponseDTO {
    
    private Long id;
    private String codigo;
    private Long idAgenda;
    private Integer idMedioPago;
    private Integer idUsuario;
    private BigDecimal monto;
    private LocalDateTime fechaPago;
    private String observaciones;
    
    // Datos adicionales del SP
    private BigDecimal totalAbonado;
    private BigDecimal saldoPendiente;
    
    // Mensaje de respuesta del SP
    private String mensaje;
}
