package com.vet.manadawoof.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HorarioBaseRequestDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    private String descripcion;
    
    private LocalTime horaInicio;
    
    private LocalTime horaFin;
    
    private Integer minutoToleranciaEntrada;
    
    @NotNull(message = "Los minutos de lunch son obligatorios")
    private Integer minutosLunch = 60;
    
    @NotNull(message = "El campo overnight es obligatorio")
    private Boolean overnight = false;
    
    @NotNull(message = "El campo activo es obligatorio")
    private Boolean activo = true;
}
