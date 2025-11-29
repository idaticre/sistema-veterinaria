package com.vet.manadawoof.mapper;

import com.vet.manadawoof.dtos.response.PagoAgendaResponseDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Component
public class PagoAgendaMapper {
    
    /**
     * Mapea una fila de resultado SQL a PagoAgendaResponseDTO
     * Usado en listarPorAgenda() y obtenerPorId()
     */
    public PagoAgendaResponseDTO toDto(Object[] row) {
        return PagoAgendaResponseDTO.builder()
                .id(((Number) row[0]).longValue())
                .codigo((String) row[1])
                .idAgenda(((Number) row[2]).longValue())
                .idMedioPago(((Number) row[3]).intValue())
                .idUsuario(row[4] != null ? ((Number) row[4]).intValue() : null)
                .monto((BigDecimal) row[5])
                .fechaPago(row[6] != null ? ((Timestamp) row[6]).toLocalDateTime() : null)
                .observaciones((String) row[7])
                .build();
    }
    
    /**
     * Mapea con totales calculados (para CREATE/DELETE)
     */
    public PagoAgendaResponseDTO toDto(Object[] row, BigDecimal totalAbonado,
                                       BigDecimal saldoPendiente, String mensaje
    ) {
        PagoAgendaResponseDTO dto = toDto(row);
        dto.setTotalAbonado(totalAbonado);
        dto.setSaldoPendiente(saldoPendiente);
        dto.setMensaje(mensaje);
        return dto;
    }
}
