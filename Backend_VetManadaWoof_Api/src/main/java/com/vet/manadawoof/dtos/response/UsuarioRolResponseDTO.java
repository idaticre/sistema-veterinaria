package com.vet.manadawoof.dtos.response;

import lombok.*;

// DTO específico para mostrar la relación usuario-rol
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioRolResponseDTO {
    
    private String username;     // Nombre de usuario (no ID)
    private String rol;          // Nombre del rol (no ID)
    private String accion;       // Acción realizada: 'ASIGNAR', 'ELIMINAR' o 'ACTUALIZAR'
    private String mensaje;      // Mensaje devuelto por el SP
}
