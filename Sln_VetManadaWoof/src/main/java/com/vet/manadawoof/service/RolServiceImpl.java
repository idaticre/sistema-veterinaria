package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.RolEntity;
import com.vet.manadawoof.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RolServiceImpl implements RolService {

    private final RolRepository repository;

    @Override
    public String crearRol(RolEntity rol) {
        return repository.spRoles("CREATE", null, rol.getNombre(), rol.getDescripcion(), rol.getActivo());
    }

    @Override
    public List<RolEntity> listarRoles() {
        repository.spRoles("READ", null, null, null, null);
        return repository.findAll();
    }

    @Override
    public String actualizarRol(RolEntity rol) {
        return repository.spRoles("UPDATE", rol.getId().intValue(), rol.getNombre(), rol.getDescripcion(), rol.getActivo());
    }

    @Override
    public String eliminarRol(Long id) {
        return repository.spRoles("DELETE", id.intValue(), null, null, null);
    }
}
