package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.entity.EspecialidadEntity;
import com.vet.manadawoof.repository.EspecialidadRepository;
import com.vet.manadawoof.service.EspecialidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EspecialidadServiceImpl implements EspecialidadService {

    private final EspecialidadRepository repository;

    @Override
    @Transactional
    public EspecialidadEntity crearEspecialidad(EspecialidadEntity entity) {
        // Evitar duplicados por nombre
        repository.findAll().stream()
                .filter(e -> e.getNombre().equalsIgnoreCase(entity.getNombre()))
                .findFirst()
                .ifPresent(e -> { throw new RuntimeException("Especialidad ya existe"); });

        return repository.save(entity);
    }

    @Override
    @Transactional
    public EspecialidadEntity actualizarEspecialidad(EspecialidadEntity entity) {
        EspecialidadEntity existente = repository.findById(entity.getId())
                .orElseThrow(() -> new RuntimeException("Especialidad no encontrada"));

        // Evitar duplicados
        repository.findAll().stream()
                .filter(e -> e.getNombre().equalsIgnoreCase(entity.getNombre()) && !e.getId().equals(entity.getId()))
                .findFirst()
                .ifPresent(e -> { throw new RuntimeException("Otra especialidad con ese nombre ya existe"); });

        existente.setNombre(entity.getNombre());
        existente.setActivo(entity.getActivo());
        return repository.save(existente);
    }

    @Override
    @Transactional
    public String eliminarEspecialidad(Integer id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Especialidad no encontrada");
        }
        repository.deleteById(id);
        return "Especialidad eliminada correctamente";
    }

    @Override
    @Transactional(readOnly = true)
    public List<EspecialidadEntity> listarEspecialidades() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public EspecialidadEntity obtenerPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Especialidad no encontrada"));
    }
}
