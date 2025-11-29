package com.vet.manadawoof.dtos.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgendaRequestDTO {
    
    // Para ACTUALIZAR (obligatorio en UPDATE)
    private Long id;
    
    // --- Datos obligatorios ---
    @NotNull(message = "El cliente es obligatorio")
    private Long idCliente;
    
    @NotNull(message = "La mascota es obligatoria")
    private Long idMascota;
    
    @NotNull(message = "La fecha es obligatoria")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fecha;
    
    @NotNull(message = "La hora es obligatoria")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime hora;
    
    @NotNull(message = "El estado es obligatorio")
    private Integer idEstado;
    
    // --- Datos opcionales ---
    private Integer idMedioSolicitud;
    
    @Min(value = 0, message = "La duración no puede ser negativa")
    private Integer duracionEstimadaMin;
    
    @DecimalMin(value = "0.0", message = "El abono no puede ser negativo")
    private BigDecimal abonoInicial;
    
    @Size(max = 256, message = "Las observaciones no deben superar 256 caracteres")
    private String observaciones;
}
