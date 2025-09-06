package com.vet.manadawoof.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioResponseDTO {
    private Integer idUsuario;
    private String codigoUsuario;
    private String username;
    private Boolean activo;
    private String mensaje;
}
