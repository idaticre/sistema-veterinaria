package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.RolEntity;
import java.util.List;

public interface RolService {

    String createRol(RolEntity rol);

    List<RolEntity> readRoles(Long id);

    String updateRol(RolEntity rol);

    String deleteRol(Long id);
}
