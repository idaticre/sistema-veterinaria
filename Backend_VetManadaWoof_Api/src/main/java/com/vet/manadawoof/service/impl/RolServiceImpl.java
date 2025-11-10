package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.entity.RolEntity;
import com.vet.manadawoof.repository.RolRepository;
import com.vet.manadawoof.service.RolService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RolServiceImpl implements RolService {

    private final RolRepository repository;

    @Override
    @Transactional
    public RolEntity crearRol(RolEntity rol) {
        return repository.save(rol);
    }

    @Override
    @Transactional
    public RolEntity actualizarRol(Integer id, RolEntity rol) {
        RolEntity existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("RolEntity no encontrado con id " + id));

        existente.setNombre(rol.getNombre());
        existente.setDescripcion(rol.getDescripcion());
        // relaciones se mantienen intactas
        return repository.save(existente);
    }

    @Override
    @Transactional
    public void eliminarRol(Integer id) {
        RolEntity existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("RolEntity no encontrado con id " + id));
        repository.delete(existente);
    }

    @Override
    @Transactional
    public RolEntity obtenerPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("RolEntity no encontrado con id " + id));
    }

    @Override
    @Transactional
    public List<RolEntity> listarRoles() {
        return repository.findAll();
    }
}
