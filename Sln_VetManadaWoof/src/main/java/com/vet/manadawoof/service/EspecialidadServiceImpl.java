package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.EspecialidadEntity;
import com.vet.manadawoof.repository.EspecialidadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EspecialidadServiceImpl implements EspecialidadService {

    private final EspecialidadRepository repository;

    @Override
    public String crearEspecialidad(EspecialidadEntity especialidad) {
        return repository.spEspecialidades("CREATE", null, especialidad.getNombre(), especialidad.getActivo());
    }

    @Override
    public String actualizarEspecialidad(EspecialidadEntity especialidad) {
        return repository.spEspecialidades("UPDATE", especialidad.getId(), especialidad.getNombre(), especialidad.getActivo());
    }

    @Override
    public String eliminarEspecialidad(Long id) {
        return repository.spEspecialidades("DELETE", id, null, null);
    }

    @Override
    public List<EspecialidadEntity> listarEspecialidades() {
        repository.spEspecialidades("READ", null, null, null);
        return repository.findAll();
    }
}