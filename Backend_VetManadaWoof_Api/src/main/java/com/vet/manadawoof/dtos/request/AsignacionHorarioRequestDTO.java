package com.vet.manadawoof.dtos.request;

import lombok.*;

import jakarta.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AsignacionHorarioRequestDTO {
    
    @NotNull(message = "ID de colaborador es obligatorio")
    private Long idColaborador;
    
    @NotNull(message = "ID de horario base es obligatorio")
    private Integer idHorarioBase;
    
    @NotNull(message = "ID de día es obligatorio")
    private Integer idDia;
    
    private Boolean activo; // opcional, por defecto true
}
