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
public class HorarioBaseRolResponseDTO {
    private Long id;
    private Integer idRol;
    private String nombreRol;
    private Integer idHorarioBase;
    private String nombreHorarioBase;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Integer idDiaSemana;
    private String nombreDia;
    private Integer ordenDia;
}
