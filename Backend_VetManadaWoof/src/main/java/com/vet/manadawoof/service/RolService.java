package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.RolEntity;

import java.util.List;
import java.util.Optional;

public interface RolService {

    String crearRol(RolEntity rol);

    List<RolEntity> listarRoles();

    String actualizarRol(RolEntity rol);

    String eliminarRol(Integer id);

    Optional<RolEntity> obtenerPorId(Integer id);
}
