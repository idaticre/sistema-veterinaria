package com.vet.manadawoof.service;

import com.vet.manadawoof.dtos.request.VeterinarioRequestDTO;
import com.vet.manadawoof.dtos.response.VeterinarioResponseDTO;

import java.util.List;

public interface VeterinarioService {
    VeterinarioResponseDTO registrarVeterinario(VeterinarioRequestDTO dto);
    VeterinarioResponseDTO actualizarVeterinario(VeterinarioRequestDTO dto);
    VeterinarioResponseDTO obtenerPorId(Integer id);
    List<VeterinarioResponseDTO> listar();
}
