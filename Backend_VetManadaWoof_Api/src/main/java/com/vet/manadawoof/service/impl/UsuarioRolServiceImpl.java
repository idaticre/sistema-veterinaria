package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.entity.UsuarioRolEntity;
import com.vet.manadawoof.repository.UsuarioRepository;
import com.vet.manadawoof.repository.RolRepository;
import com.vet.manadawoof.repository.UsuarioRolRepository;
import com.vet.manadawoof.service.UsuarioRolService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioRolServiceImpl implements UsuarioRolService {

    private final UsuarioRolRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    @Override
    @Transactional
    public UsuarioRolEntity crearUsuarioRol(UsuarioRolEntity entity) {
        // Verificar existencia de usuario y rol
        usuarioRepository.findById(entity.getUsuario().getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        rolRepository.findById(entity.getRol().getId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        return repository.save(entity);
    }

    @Override
    @Transactional
    public void eliminarUsuarioRol(Integer id, Integer usuarioId, Integer rolId) {
        if (id != null) {
            repository.deleteById(id);
        } else if (usuarioId != null && rolId != null) {
            repository.deleteByUsuarioIdAndRolId(usuarioId, rolId);
        } else {
            throw new RuntimeException("Debe proporcionar id o usuarioId + rolId");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioRolEntity> listar() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioRolEntity> listarRolesPorUsuario(Integer usuarioId) {
        return repository.findByUsuarioId(usuarioId);
    }
}
