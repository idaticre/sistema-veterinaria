package com.vet.manadawoof.service;

import com.vet.manadawoof.dtos.request.ClienteRequestDTO;
import com.vet.manadawoof.dtos.response.ClienteResponseDTO;

import java.util.List;

public interface ClienteService {
    ClienteResponseDTO registrar(ClienteRequestDTO dto);

    ClienteResponseDTO actualizar(ClienteRequestDTO dto);

    List<ClienteResponseDTO> listar();

    ClienteResponseDTO obtenerPorId(Long id);

    // borrado lógico
    ClienteResponseDTO eliminar(Long idCliente);

}
