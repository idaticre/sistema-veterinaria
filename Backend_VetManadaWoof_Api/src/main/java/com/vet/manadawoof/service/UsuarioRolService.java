package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.UsuarioRolEntity;

import java.util.List;

public interface UsuarioRolService {

    UsuarioRolEntity crearUsuarioRol(UsuarioRolEntity entity);

    void eliminarUsuarioRol(Integer id, Integer usuarioId, Integer rolId);

    List<UsuarioRolEntity> listar();

    List<UsuarioRolEntity> listarRolesPorUsuario(Integer usuarioId);
}
