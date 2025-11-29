package com.vet.manadawoof.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PagoAgendaRequestDTO {
    
    // Para eliminación
    private Long idPago;
    
    @NotNull(message = "Agenda es obligatoria")
    private Long idAgenda;
    
    @NotNull(message = "Medio de pago es obligatorio")
    private Integer idMedioPago;
    
    private Integer idUsuario;
    
    @NotNull(message = "Monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal monto;
    
    @Size(max = 128, message = "Observaciones no debe superar 128 caracteres")
    private String observaciones;
}
