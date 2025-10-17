package com.vet.manadawoof.dtos.response;

import lombok.*;

import java.time.LocalTime;

/**
 * DTO para representar un horario de trabajo en las respuestas del API.
 * Evita exponer internamente la entidad completa.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HorarioTrabajoResponseDTO {
    private Integer id;
    private String nombre;
    private String descripcion;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Boolean activo;
}
