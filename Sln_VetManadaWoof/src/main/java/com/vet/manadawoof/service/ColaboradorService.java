package com.vet.manadawoof.service;

import com.vet.manadawoof.dtos.request.ColaboradorRequestDTO;
import com.vet.manadawoof.dtos.response.ColaboradorResponseDTO;

import java.util.List;

public interface ColaboradorService {

    ColaboradorResponseDTO registrarColaborador(ColaboradorRequestDTO dto);

    ColaboradorResponseDTO actualizarColaborador(ColaboradorRequestDTO dto);

    ColaboradorResponseDTO buscarPorId(Integer id);

    List<ColaboradorResponseDTO> listarColaboradores();
}
