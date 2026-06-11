package com.vet.manadawoof.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HorarioRequestDTO {

    @NotNull(message = "El ID del trabajador es obligatorio")
    private Long trabajadorId;

    @NotNull(message = "El ID del día es obligatorio")
    private Integer diaId;

    @NotNull(message = "El campo trabaja es obligatorio")
    private Boolean trabaja;

    private LocalTime horaInicio;

    private LocalTime horaFin;
}
