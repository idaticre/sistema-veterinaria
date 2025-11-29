package com.vet.manadawoof.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioRolRequestDTO {
    
    private String accion;       // 'ASIGNAR', 'ELIMINAR' o 'ACTUALIZAR'
    private Integer idUsuario;
    private Integer idRol;
}
