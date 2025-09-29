package com.vet.manadawoof.service;

import com.vet.manadawoof.dtos.request.ProveedorRequestDTO;
import com.vet.manadawoof.dtos.response.ProveedorResponseDTO;

import java.util.List;

public interface ProveedorService {
    ProveedorResponseDTO registrar(ProveedorRequestDTO dto);

    ProveedorResponseDTO actualizar(ProveedorRequestDTO dto);

    ProveedorResponseDTO obtenerPorId(Long id);

    List<ProveedorResponseDTO> listar();
}
