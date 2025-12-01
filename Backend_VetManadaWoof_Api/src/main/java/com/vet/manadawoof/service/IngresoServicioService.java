package com.vet.manadawoof.service;

import com.vet.manadawoof.dtos.request.IngresoServicioRequestDTO;
import com.vet.manadawoof.dtos.response.IngresoServicioResponseDTO;

import java.util.List;

public interface IngresoServicioService {
    
    // Crea un nuevo servicio para una cita
    IngresoServicioResponseDTO crear(IngresoServicioRequestDTO dto);
    
    // Actualiza un servicio existente
    IngresoServicioResponseDTO actualizar(IngresoServicioRequestDTO dto);
    
    // Elimina un servicio
    IngresoServicioResponseDTO eliminar(Long idIngreso, Long idAgenda);
    
    IngresoServicioResponseDTO obtenerPorId(Long id);
    
    // Lista servicios de una cita
    List<IngresoServicioResponseDTO> listarPorAgenda(Long idAgenda);
}
