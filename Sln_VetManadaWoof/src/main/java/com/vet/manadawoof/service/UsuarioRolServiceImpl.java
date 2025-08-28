package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.UsuarioRolEntity;
import com.vet.manadawoof.repository.UsuarioRolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioRolServiceImpl implements UsuarioRolService {

    private final UsuarioRolRepository repository;

    @Override
    public String crearRolUsuario(Long usuarioId, Long rolId) {
        return repository.spUsuariosRoles("CREATE", null, usuarioId, rolId);
    }

    @Override
    public String eliminarRolUsuario(Long usuarioId, Long rolId) {
        return repository.spUsuariosRoles("DELETE", null, usuarioId, rolId);
    }

    @Override
    public List<UsuarioRolEntity> listarRolesUsuario(Long usuarioId) {
        repository.spUsuariosRoles("READ", null, usuarioId, null);
        // Solo para devolver algo, aunque la consulta del SP no devuelve lista mapeada
        return repository.findAll();
    }
}
