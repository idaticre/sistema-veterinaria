package com.vet.manadawoof.service;

import com.vet.manadawoof.dtos.request.UsuarioRolRequestDTO;
import com.vet.manadawoof.dtos.response.UsuarioRolResponseDTO;

import java.util.List;

public interface UsuarioRolService {
    
    // Ejecuta la acción de SP: ASIGNAR o ACTUALIZAR
    UsuarioRolResponseDTO ejecutarAccion(UsuarioRolRequestDTO dto);
    
    // Lista todos los usuarios con sus roles (solo nombres)
    List<UsuarioRolResponseDTO> listar();
    
    // Lista roles asignados a un usuario específico (solo nombres)
    List<UsuarioRolResponseDTO> listarPorUsuario(Integer idUsuario);
    
    // Elimina una asignación usuario-rol
    UsuarioRolResponseDTO eliminar(Integer idUsuario, Integer idRol);
}
