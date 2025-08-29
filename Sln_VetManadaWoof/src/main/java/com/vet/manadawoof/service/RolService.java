package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.RolEntity;
import java.util.List;

public interface RolService {

    String crearRol(RolEntity rol);

    List<RolEntity> listarRoles();

    String actualizarRol(RolEntity rol);

    String eliminarRol(Long id);
}
