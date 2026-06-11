package com.vet.manadawoof.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HorarioResponseDTO {

    private Long id;
    private Long trabajadorId;
    private String nombreColaborador;
    private Integer diaId;
    private String nombreDia;
    private Boolean trabaja;
    private LocalTime horaInicio;
    private LocalTime horaFin;
}
