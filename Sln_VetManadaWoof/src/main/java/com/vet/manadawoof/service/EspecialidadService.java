package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.EspecialidadEntity;
import java.util.List;

public interface EspecialidadService {

    String crearEspecialidad(EspecialidadEntity especialidad);

    String actualizarEspecialidad(EspecialidadEntity especialidad);

    String eliminarEspecialidad(Long id);

    List<EspecialidadEntity> listarEspecialidades();
}
