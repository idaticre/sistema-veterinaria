package com.vet.manadawoof.dtos.response;

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
public class GestionDiaEspecialResponseDTO {
    private String status;
    private String mensaje;
    private Long idColaborador;
    private String nombreColaborador;
    private LocalDate fecha;
    private String diaSemana;
    private String tipoAccion;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Boolean esExcepcion;
}
