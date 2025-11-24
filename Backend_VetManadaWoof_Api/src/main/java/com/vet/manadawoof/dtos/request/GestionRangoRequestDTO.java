package com.vet.manadawoof.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GestionRangoRequestDTO {
    
    @NotNull(message = "El ID del colaborador es obligatorio")
    private Long idColaborador;
    
    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;
    
    @NotNull(message = "La fecha fin es obligatoria")
    private LocalDate fechaFin;
    
    // HORARIO, DESCANSO, DESASIGNAR
    @NotNull(message = "El tipo de acción es obligatorio")
    private String tipoAccion;
    
    private LocalTime horaInicio; // Requerido solo para HORARIO
    private LocalTime horaFin;    // Requerido solo para HORARIO
    
    @NotNull(message = "El ID del horario base es obligatorio")
    private Integer idHorarioBase;
    
    @NotNull(message = "El ID del usuario que realiza la acción es obligatorio")
    private Long idUsuario;
    
    private String motivo;
}
