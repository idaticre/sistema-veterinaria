package com.vet.manadawoof.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DiaResponseDTO {
    private Integer idDia;
    private String codigo;
    private String nombre;
    private Boolean activo;
    // Incluye nombre del tipo de día
    private String tipoDiaNombre;
    private String mensaje;
}
