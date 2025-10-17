package com.vet.manadawoof.service;

import com.vet.manadawoof.dtos.request.EntidadRequestDTO;
import com.vet.manadawoof.dtos.response.EntidadResponseDTO;

import java.util.List;

public interface EntidadService {

    List<EntidadResponseDTO> listarEntidades();

    EntidadResponseDTO crearEntidad(EntidadRequestDTO request);

    EntidadResponseDTO actualizarEntidad(EntidadRequestDTO request);

    EntidadResponseDTO eliminarEntidad(Long id);
}
