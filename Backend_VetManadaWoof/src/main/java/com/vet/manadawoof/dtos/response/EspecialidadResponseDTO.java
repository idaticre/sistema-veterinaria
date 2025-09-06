package com.vet.manadawoof.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EspecialidadResponseDTO {
    private Integer id;
    private String codigo;
    private String nombre;
    private Boolean activo;
    private String mensaje;
}
