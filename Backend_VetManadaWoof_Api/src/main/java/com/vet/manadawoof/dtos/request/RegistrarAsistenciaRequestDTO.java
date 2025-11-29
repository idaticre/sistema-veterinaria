package com.vet.manadawoof.dtos.request;

import com.vet.manadawoof.enums.TipoMarca;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrarAsistenciaRequestDTO {
    private Long idColaborador;
    
    // ENTRADA, LUNCH_IN, LUNCH_OUT, SALIDA
    private TipoMarca tipoMarca;
}
