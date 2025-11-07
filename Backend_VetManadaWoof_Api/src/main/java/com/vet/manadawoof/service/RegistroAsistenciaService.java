package com.vet.manadawoof.service;

import com.vet.manadawoof.dtos.request.RegistrarAsistenciaRequestDTO;
import com.vet.manadawoof.dtos.response.RegistroAsistenciaResponseDTO;

import java.time.LocalDate;
import java.util.List;

/**
 * Servicio para gestionar la asistencia de los colaboradores.
 * Expone operaciones para registrar y consultar asistencia usando DTOs.
 */
public interface RegistroAsistenciaService {
    
    /**
     * Registra o actualiza la asistencia de un colaborador.
     *
     * @param request DTO con id del colaborador y tipo de marca.
     * @return mensaje de éxito o error.
     */
    String registrar(RegistrarAsistenciaRequestDTO request);
    
    /**
     * Obtiene la lista de asistencias de los colaboradores en un rango de fechas.
     *
     * @param fechaInicio Fecha inicial del rango
     * @param fechaFin    Fecha final del rango
     * @return Lista de registros de asistencia mapeados a DTOs
     */
    List<RegistroAsistenciaResponseDTO> verAsistenciaPorRango(LocalDate fechaInicio, LocalDate fechaFin, Integer idEstado);
}
