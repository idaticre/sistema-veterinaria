package com.vet.manadawoof.mapper;

import com.vet.manadawoof.dtos.response.RegistroAsistenciaResponseDTO;
import com.vet.manadawoof.entity.RegistroAsistenciaEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entidades de RegistroAsistencia a DTOs de respuesta.
 */
public class RegistroAsistenciaMapper {
    
    /**
     * Convierte una entidad a DTO.
     */
    public static RegistroAsistenciaResponseDTO toDTO(RegistroAsistenciaEntity entity) {
        if(entity == null) return null;
        
        return RegistroAsistenciaResponseDTO.builder()
                .idColaborador(entity.getColaborador().getId())
                .horario(entity.getHorarioBase() != null ? entity.getHorarioBase().getNombre() : null)
                .fecha(entity.getFecha())
                .horaEntrada(entity.getHoraEntrada())
                .horaLunchInicio(entity.getHoraLunchInicio())
                .horaLunchFin(entity.getHoraLunchFin())
                .horaSalida(entity.getHoraSalida())
                .estadoAsistencia(entity.getEstadoAsistencia() != null
                        ? entity.getEstadoAsistencia().getNombre()
                        : null)
                .build();
    }
    
    /**
     * Convierte una lista de entidades a lista de DTOs.
     */
    public static List<RegistroAsistenciaResponseDTO> toDTOList(List<RegistroAsistenciaEntity> entities) {
        return entities.stream()
                .map(RegistroAsistenciaMapper :: toDTO)
                .collect(Collectors.toList());
    }
}
