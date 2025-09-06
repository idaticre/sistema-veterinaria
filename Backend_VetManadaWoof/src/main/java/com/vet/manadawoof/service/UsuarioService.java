package com.vet.manadawoof.service;

import com.vet.manadawoof.dtos.response.UsuarioResponseDTO;
import com.vet.manadawoof.entity.UsuarioEntity;

import java.util.List;

public interface UsuarioService {

    UsuarioResponseDTO createUser(UsuarioEntity user);

    UsuarioResponseDTO updateUser(UsuarioEntity user);

    UsuarioResponseDTO deleteUser(Integer id);

    UsuarioResponseDTO findUserById(Integer id);

    List<UsuarioResponseDTO> findAllUsers();
}
