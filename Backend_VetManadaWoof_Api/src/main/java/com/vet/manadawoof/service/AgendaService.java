package com.vet.manadawoof.service;

import com.vet.manadawoof.dtos.request.AgendaRequestDTO;
import com.vet.manadawoof.dtos.response.AgendaResponseDTO;

import java.util.List;

public interface AgendaService {
    
    // Crea una nueva cita en la agenda
    AgendaResponseDTO crear(AgendaRequestDTO dto);
    
    // Actualiza una cita existente
    AgendaResponseDTO actualizar(AgendaRequestDTO dto);
    
    // Lista todas las citas
    List<AgendaResponseDTO> listar();
    
    // Obtiene una cita por ID
    AgendaResponseDTO obtenerPorId(Long idAgenda);
}
