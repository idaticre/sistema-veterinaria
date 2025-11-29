package com.vet.manadawoof.mapper;

import com.vet.manadawoof.dtos.response.IngresoServicioResponseDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Component
public class IngresoServicioMapper {
    
    /**
     * Mapea una fila de resultado SQL a IngresoServicioResponseDTO
     * Usado en listarPorAgenda() y obtenerPorId()
     */
    public IngresoServicioResponseDTO toDto(Object[] row) {
        return IngresoServicioResponseDTO.builder()
                .id(((Number) row[0]).longValue())
                .codigo((String) row[1])
                .idAgenda(((Number) row[2]).longValue())
                .idServicio(((Number) row[3]).intValue())
                .idColaborador(row[4] != null ? ((Number) row[4]).longValue() : null)
                .idVeterinario(row[5] != null ? ((Number) row[5]).longValue() : null)
                .cantidad(((Number) row[6]).intValue())
                .duracionMin(row[7] != null ? ((Number) row[7]).intValue() : null)
                .valorServicio((BigDecimal) row[8])
                .observaciones((String) row[9])
                .fechaRegistro(row[10] != null ? ((Timestamp) row[10]).toLocalDateTime() : null)
                .build();
    }
    
    /**
     * Mapea con mensaje y nuevo total de cita (para CREATE/UPDATE)
     */
    public IngresoServicioResponseDTO toDto(Object[] row, BigDecimal nuevoTotalCita, String mensaje) {
        IngresoServicioResponseDTO dto = toDto(row);
        dto.setNuevoTotalCita(nuevoTotalCita);
        dto.setMensaje(mensaje);
        return dto;
    }
}
