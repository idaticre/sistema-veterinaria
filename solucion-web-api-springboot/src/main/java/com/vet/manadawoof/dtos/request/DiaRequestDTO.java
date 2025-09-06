package com.vet.manadawoof.dtos.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DiaRequestDTO {
    private Integer idDia;
    private String nombre;
    private Boolean activo;
    private Integer idTipoDia;
}
