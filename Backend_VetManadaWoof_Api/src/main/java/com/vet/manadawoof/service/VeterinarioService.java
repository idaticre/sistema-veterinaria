package com.vet.manadawoof.service;

import com.vet.manadawoof.dtos.request.VeterinarioRequestDTO;
import com.vet.manadawoof.dtos.response.VeterinarioResponseDTO;

import java.util.List;

public interface VeterinarioService {

    VeterinarioResponseDTO registrar(VeterinarioRequestDTO dto);

    VeterinarioResponseDTO actualizar(Long idVeterinario, VeterinarioRequestDTO dto);

    VeterinarioResponseDTO obtenerPorId(Long id);

    List<VeterinarioResponseDTO> listar();
}
