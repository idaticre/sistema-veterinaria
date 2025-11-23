package com.vet.manadawoof.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FiltroAsistenciaRequestDTO {
    @NotNull(message = "Fecha inicio es obligatoria")
    private LocalDate fechaInicio;
    
    @NotNull(message = "Fecha fin es obligatoria")
    private LocalDate fechaFin;
    
    private Long idColaborador;
    
    private Integer idEstado;
}
