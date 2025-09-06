package com.vet.manadawoof.dtos.request;

import lombok.Data;

@Data
public class UsuarioRolRequestDTO {
    // "CREATE", "READ", "DELETE"
    private String accion;
    private Integer usuarioId;
    private Integer rolId;
}
