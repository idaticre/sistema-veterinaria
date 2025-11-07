package com.vet.manadawoof.dtos.request;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FiltroAsistenciaRequestDTO {
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    
    // opcional para filtrar por estado si luego lo implementas
    private Integer idEstado;
}
