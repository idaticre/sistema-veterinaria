package com.vet.manadawoof.service;

import com.vet.manadawoof.dtos.response.EspecialidadResponseDTO;
import com.vet.manadawoof.entity.EspecialidadEntity;
import java.util.List;

public interface EspecialidadService {

    EspecialidadResponseDTO crearEspecialidad(EspecialidadEntity especialidad);

    EspecialidadResponseDTO actualizarEspecialidad(EspecialidadEntity especialidad);

    EspecialidadResponseDTO eliminarEspecialidad(Integer id);

    List<EspecialidadResponseDTO> listarEspecialidades();
}
