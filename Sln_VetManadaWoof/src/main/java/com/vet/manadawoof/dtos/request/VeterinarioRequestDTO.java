package com.vet.manadawoof.dtos.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VeterinarioRequestDTO {
    private Integer id;
    private String cmp;
    private Boolean activo;
    private Integer idEspecialidad;

    private ColaboradorRequestDTO colaborador;
}
