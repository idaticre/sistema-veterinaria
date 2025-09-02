package com.vet.manadawoof.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioRolResponseDTO {
    private Integer id;
    private Integer usuarioId;
    private Integer rolId;
    private String mensaje;
}
