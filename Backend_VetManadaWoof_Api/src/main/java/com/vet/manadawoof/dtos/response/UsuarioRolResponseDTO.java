package com.vet.manadawoof.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioRolResponseDTO {
    private Integer idUsuario;
    private String username;
    private String rol;
    private String fechaAsignacion;
    private String accion;       // Acción realizada: 'ASIGNAR', 'ELIMINAR' o 'ACTUALIZAR'
    private String mensaje;
}
