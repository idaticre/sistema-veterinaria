package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.EspecialidadEntity;

import java.util.List;

public interface EspecialidadService {

    EspecialidadEntity crearEspecialidad(EspecialidadEntity entity);

    EspecialidadEntity actualizarEspecialidad(EspecialidadEntity entity);

    String eliminarEspecialidad(Integer id);

    List<EspecialidadEntity> listarEspecialidades();

    EspecialidadEntity obtenerPorId(Integer id);
}
