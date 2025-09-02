package com.vet.manadawoof.service;

import com.vet.manadawoof.dtos.request.DiaRequestDTO;
import com.vet.manadawoof.dtos.response.DiaResponseDTO;
import com.vet.manadawoof.entity.DiaEntity;

import java.util.List;

public interface DiaService {
    DiaResponseDTO crearDia(DiaRequestDTO dto);

    DiaResponseDTO actualizarDia(DiaRequestDTO dto);

    DiaResponseDTO eliminarDia(Integer id);

    List<DiaResponseDTO> listarDias();

    DiaEntity findById(Integer id);
}
