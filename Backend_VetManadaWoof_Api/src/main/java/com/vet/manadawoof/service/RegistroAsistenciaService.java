package com.vet.manadawoof.service;

import com.vet.manadawoof.dtos.request.RegistrarAsistenciaRequestDTO;
import com.vet.manadawoof.dtos.response.RegistroAsistenciaResponseDTO;

import java.time.LocalDate;
import java.util.List;

// Servicio para gestionar la asistencia de los colaboradores.

public interface RegistroAsistenciaService {
    
    RegistroAsistenciaResponseDTO registrar(RegistrarAsistenciaRequestDTO request);
    
    List<RegistroAsistenciaResponseDTO> verAsistenciaPorRango(
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Long idColaborador,
            Integer idEstado
    );
}
