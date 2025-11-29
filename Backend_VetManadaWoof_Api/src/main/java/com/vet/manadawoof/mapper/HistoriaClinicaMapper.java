package com.vet.manadawoof.mapper;

import com.vet.manadawoof.dtos.response.HistoriaClinicaResponseDTO;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Timestamp;

@Component
public class HistoriaClinicaMapper {
    
    /**
     * Mapea una fila de resultado SQL a HistoriaClinicaResponseDTO
     * Usado en crear() y consultas
     */
    public HistoriaClinicaResponseDTO toDto(Object[] row) {
        return HistoriaClinicaResponseDTO.builder()
                .id(((Number) row[0]).longValue())
                .codigo((String) row[1])
                .idMascota(((Number) row[2]).longValue())
                .fechaApertura(row[3] != null ? ((Date) row[3]).toLocalDate() : null)
                .observacionesGenerales((String) row[4])
                .activa(row[5] != null && ((Number) row[5]).intValue() == 1)
                .fechaRegistro(row[6] != null ? ((Timestamp) row[6]).toLocalDateTime() : null)
                .build();
    }
    
    /**
     * Mapea con mensaje personalizado (para CREATE)
     */
    public HistoriaClinicaResponseDTO toDto(Object[] row, String mensaje) {
        HistoriaClinicaResponseDTO dto = toDto(row);
        dto.setMensaje(mensaje);
        return dto;
    }
}
