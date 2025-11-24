package com.vet.manadawoof.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AsignacionHorarioRequestDTO {
    
    @NotNull(message = "El ID del colaborador es obligatorio")
    private Long idColaborador;
    
    @NotNull(message = "El ID del horario base es obligatorio")
    private Integer idHorarioBase;
    
    @NotNull(message = "El ID del día es obligatorio")
    private Integer idDiaSemana;
    
    @NotNull(message = "La fecha de inicio de vigencia es obligatoria")
    private LocalDate fechaInicioVigencia;
    
    private LocalDate fechaFinVigencia;
    
    private String motivoCambio;
}
