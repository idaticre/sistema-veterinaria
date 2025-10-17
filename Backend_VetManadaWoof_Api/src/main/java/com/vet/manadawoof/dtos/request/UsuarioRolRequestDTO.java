package com.vet.manadawoof.dtos.request;

import lombok.*;

// DTO para recibir solicitudes de asignación/actualización/eliminación
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioRolRequestDTO {
    
    private String accion;       // 'ASIGNAR', 'ELIMINAR' o 'ACTUALIZAR'
    private Integer idUsuario;   // ID del usuario
    private Integer idRol;       // ID del rol
}
