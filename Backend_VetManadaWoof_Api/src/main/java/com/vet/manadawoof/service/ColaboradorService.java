package com.vet.manadawoof.service;

import com.vet.manadawoof.dtos.request.ColaboradorRequestDTO;
import com.vet.manadawoof.dtos.response.ColaboradorResponseDTO;

import java.util.List;

// Interfaz
public interface ColaboradorService {
    ColaboradorResponseDTO registrar(ColaboradorRequestDTO dto);

    ColaboradorResponseDTO actualizar(Long idColaborador, ColaboradorRequestDTO dto);

    List<ColaboradorResponseDTO> listar();

    ColaboradorResponseDTO buscarPorId(Long idColaborador);
}

