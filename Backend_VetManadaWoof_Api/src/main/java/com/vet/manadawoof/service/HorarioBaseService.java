package com.vet.manadawoof.service;

import com.vet.manadawoof.dtos.request.HorarioBaseRequestDTO;
import com.vet.manadawoof.dtos.response.HorarioBaseResponseDTO;

import java.util.List;


// Servicio para manejar los horarios de trabajo.
public interface HorarioBaseService {
    
    HorarioBaseResponseDTO crearHorario(HorarioBaseRequestDTO request);
    
    HorarioBaseResponseDTO actualizarHorario(Integer id, HorarioBaseRequestDTO request);
    
    void eliminarHorario(Integer id);
    
    List<HorarioBaseResponseDTO> listarHorarios();
    
    HorarioBaseResponseDTO obtenerPorId(Integer id);
}
