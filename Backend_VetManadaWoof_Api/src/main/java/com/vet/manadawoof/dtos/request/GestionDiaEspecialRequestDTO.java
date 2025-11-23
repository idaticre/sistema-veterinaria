package com.vet.manadawoof.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
public class GestionDiaEspecialRequestDTO {
    @NotNull(message = "El ID del colaborador es obligatorio")
    private Long idColaborador;
    
    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;
    
    @NotNull(message = "El tipo de acción es obligatorio")
    @Pattern(regexp = "HORARIO|DESCANSO|DESASIGNAR",
            message = "Tipo de acción debe ser: HORARIO, DESCANSO o DESASIGNAR")
    private String tipoAccion;
    
    private LocalTime horaInicio;
    
    private LocalTime horaFin;
    
    private Long idUsuario;
}
