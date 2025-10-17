package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.UsuarioEntity;

import java.util.List;

public interface UsuarioService {

    UsuarioEntity crearUsuario(UsuarioEntity usuario);

    UsuarioEntity actualizarUsuario(Integer id, UsuarioEntity usuario);

    void eliminarUsuario(Integer id);

    List<UsuarioEntity> listarUsuarios();

    UsuarioEntity obtenerUsuarioPorId(Integer id);
}
