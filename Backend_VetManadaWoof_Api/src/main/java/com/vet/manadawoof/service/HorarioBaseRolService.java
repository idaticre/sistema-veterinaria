package com.vet.manadawoof.service;

import com.vet.manadawoof.dtos.request.HorarioBaseRolRequestDTO;
import com.vet.manadawoof.dtos.response.HorarioBaseRolResponseDTO;

import java.util.List;

public interface HorarioBaseRolService {
    
    HorarioBaseRolResponseDTO asignarHorarioARol(HorarioBaseRolRequestDTO request);
    
    void eliminarAsignacion(Long id);
    
    List<HorarioBaseRolResponseDTO> listarTodos();
    
    List<HorarioBaseRolResponseDTO> listarPorRol(Integer idRol);
    
    List<HorarioBaseRolResponseDTO> listarPorHorarioBase(Integer idHorarioBase);
    
    List<HorarioBaseRolResponseDTO> listarPorDia(Integer idDiaSemana);
    
    HorarioBaseRolResponseDTO obtenerPorId(Long id);
}
