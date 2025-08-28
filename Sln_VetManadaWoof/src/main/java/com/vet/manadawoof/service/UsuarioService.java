package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.UsuarioEntity;

import java.util.List;

public interface UsuarioService {
    String createUser(UsuarioEntity user);

    String updateUser(UsuarioEntity user);

    String deleteUser(Long id);

    UsuarioEntity findUserById(Long id);

    List<UsuarioEntity> findAllUsers();
}
