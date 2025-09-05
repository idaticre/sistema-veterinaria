package com.vet.manadawoof.service;

import com.vet.manadawoof.dtos.request.UsuarioRolRequestDTO;
import com.vet.manadawoof.dtos.response.UsuarioRolResponseDTO;

import java.util.List;

public interface UsuarioRolService {
    UsuarioRolResponseDTO gestionarUsuarioRol(UsuarioRolRequestDTO requestDTO);
    List<UsuarioRolResponseDTO> listarRolesPorUsuario(Integer usuarioId);
}
