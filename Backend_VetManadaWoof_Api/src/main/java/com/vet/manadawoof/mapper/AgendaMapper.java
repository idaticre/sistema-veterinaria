package com.vet.manadawoof.mapper;

import com.vet.manadawoof.dtos.response.AgendaResponseDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

@Component
public class AgendaMapper {
    
    
    public AgendaResponseDTO toDto(Object[] row) {
        return AgendaResponseDTO.builder()
                .id(((Number) row[0]).longValue())
                .codigo((String) row[1])
                .idCliente(((Number) row[2]).longValue())
                .idMascota(((Number) row[3]).longValue())
                .idMedioSolicitud(row[4] != null ? ((Number) row[4]).intValue() : null)
                .fecha(row[5] != null ? ((Date) row[5]).toLocalDate() : null)
                .hora(row[6] != null ? ((Time) row[6]).toLocalTime() : null)
                .duracionEstimadaMin(row[7] != null ? ((Number) row[7]).intValue() : null)
                .abonoInicial((BigDecimal) row[8])
                .totalCita((BigDecimal) row[9])
                .idEstado(((Number) row[10]).intValue())
                .observaciones((String) row[11])
                .fechaRegistro(row[12] != null ? ((Timestamp) row[12]).toLocalDateTime() : null)
                .build();
    }
    
    
    public AgendaResponseDTO toDto(Object[] row, String mensaje) {
        AgendaResponseDTO dto = toDto(row);
        dto.setMensaje(mensaje);
        return dto;
    }
}
