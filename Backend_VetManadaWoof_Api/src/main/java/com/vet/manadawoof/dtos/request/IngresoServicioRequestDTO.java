package com.vet.manadawoof.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
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
public class IngresoServicioRequestDTO {
    
    // Para actualización y eliminación
    private Long idIngreso;
    
    @NotNull(message = "Agenda es obligatoria")
    private Long idAgenda;
    
    @NotNull(message = "Servicio es obligatorio")
    private Integer idServicio;
    
    private Long idColaborador;
    
    private Long idVeterinario;
    
    @NotNull(message = "Cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;
    
    @Min(value = 0, message = "La duración no puede ser negativa")
    private Integer duracionMin;
    
    @NotNull(message = "Valor del servicio es obligatorio")
    @DecimalMin(value = "0.01", message = "El valor debe ser mayor a 0")
    private BigDecimal valorServicio;
    
    @Size(max = 128, message = "Observaciones no debe superar 128 caracteres")
    private String observaciones;
}
