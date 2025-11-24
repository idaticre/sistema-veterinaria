package com.vet.manadawoof.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HorarioBaseRolRequestDTO {
    @NotNull(message = "El ID del rol es obligatorio")
    private Integer idRol;
    
    @NotNull(message = "El ID del horario base es obligatorio")
    private Integer idHorarioBase;
    
    @NotNull(message = "El ID del día es obligatorio")
    private Integer idDiaSemana;
}
