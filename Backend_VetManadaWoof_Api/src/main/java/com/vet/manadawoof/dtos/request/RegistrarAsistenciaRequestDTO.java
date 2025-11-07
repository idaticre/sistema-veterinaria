package com.vet.manadawoof.dtos.request;

import com.vet.manadawoof.enums.TipoMarca;
import lombok.*;

/**
 * DTO para registrar o actualizar la asistencia de un colaborador.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrarAsistenciaRequestDTO {
    private Integer idColaborador;
    
    // ENTRADA, LUNCH_IN, LUNCH_OUT, SALIDA
    private TipoMarca tipoMarca;
}
