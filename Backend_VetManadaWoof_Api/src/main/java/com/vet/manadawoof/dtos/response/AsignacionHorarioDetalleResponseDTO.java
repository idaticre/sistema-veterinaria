package com.vet.manadawoof.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AsignacionHorarioDetalleResponseDTO {
    
    private Long idDetalle;
    private Long idAsignacion;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Boolean esExcepcion;
    private String tipoHorario; // "BASE", "PERSONALIZADO", "DESCANSO"
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
}
