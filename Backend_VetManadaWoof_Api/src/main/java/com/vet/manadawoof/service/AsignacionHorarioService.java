package com.vet.manadawoof.service;

import com.vet.manadawoof.dtos.request.AsignacionHorarioRequestDTO;
import com.vet.manadawoof.dtos.response.ApiResponse;
import com.vet.manadawoof.dtos.response.AsignacionHorarioResponseDTO;

import java.util.List;

public interface AsignacionHorarioService {
    
    // Asigna o actualiza un horario para un colaborador en un día específico
    ApiResponse<AsignacionHorarioResponseDTO> asignarHorarioDia(AsignacionHorarioRequestDTO dto);
    
    // Desasigna (desactiva) un horario para un colaborador en un día específico
    ApiResponse<AsignacionHorarioResponseDTO> desasignarHorarioDia(AsignacionHorarioRequestDTO dto);
    
    // Asigna un mismo horario para toda la semana laboral (lunes a sábado)
    ApiResponse<List<AsignacionHorarioResponseDTO>> asignarHorarioSemana(AsignacionHorarioRequestDTO dto);
    
    // Desasigna todos los horarios de lunes a sábado para un colaborador
    ApiResponse<String> desasignarHorarioSemana(Long idColaborador);
    
    // Lista todas las asignaciones de horarios existentes
    ApiResponse<List<AsignacionHorarioResponseDTO>> listarAsignaciones();
}
