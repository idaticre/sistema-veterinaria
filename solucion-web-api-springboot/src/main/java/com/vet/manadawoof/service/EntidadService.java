package com.vet.manadawoof.service;

import com.vet.manadawoof.dtos.request.EntidadRequestDTO;
import com.vet.manadawoof.dtos.response.EntidadResponseDTO;
import com.vet.manadawoof.entity.EntidadEntity;

import java.util.List;

public interface EntidadService {

    EntidadResponseDTO registrarEntidad(EntidadRequestDTO dto);

    EntidadResponseDTO actualizarEntidad(EntidadRequestDTO dto);

    EntidadEntity findById(Integer id);

    List<EntidadEntity> findAll();
}
