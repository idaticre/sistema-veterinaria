package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.UsuarioRolEntity;
import java.util.List;

public interface UsuarioRolService {

    String crearRolUsuario(Long usuarioId, Long rolId);

    String eliminarRolUsuario(Long usuarioId, Long rolId);

    List<UsuarioRolEntity> listarRolesUsuario(Long usuarioId);
}
