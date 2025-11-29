package com.vet.manadawoof.mapper;

import com.vet.manadawoof.dtos.response.AtencionMedicaResponseDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

@Component
public class AtencionMedicaMapper {
    
    /**
     * Mapea una fila de resultado SQL a AtencionMedicaResponseDTO
     * Usado en registrarAtencion(), actualizarAtencion() y consultas
     */
    public AtencionMedicaResponseDTO toDto(Object[] row) {
        return AtencionMedicaResponseDTO.builder()
                .id(((Number) row[0]).longValue())
                .codigo((String) row[1])
                .idHistoriaClinica(((Number) row[2]).longValue())
                .idAgenda(row[3] != null ? ((Number) row[3]).longValue() : null)
                .idVeterinario(row[4] != null ? ((Number) row[4]).longValue() : null)
                .idColaborador(row[5] != null ? ((Number) row[5]).longValue() : null)
                .fechaAtencion(row[6] != null ? ((Date) row[6]).toLocalDate() : null)
                .horaInicio(row[7] != null ? ((Time) row[7]).toLocalTime() : null)
                .horaFin(row[8] != null ? ((Time) row[8]).toLocalTime() : null)
                .motivoConsulta((String) row[9])
                .anamnesis((String) row[10])
                .examenFisico((String) row[11])
                .signosVitales((String) row[12])
                .pesoKg((BigDecimal) row[13])
                .temperaturaC((BigDecimal) row[14])
                .diagnostico((String) row[15])
                .tratamiento((String) row[16])
                .observaciones((String) row[17])
                .proximoControl(row[18] != null ? ((Date) row[18]).toLocalDate() : null)
                .idEstado(row[19] != null ? ((Number) row[19]).intValue() : null)
                .fechaRegistro(row[20] != null ? ((Timestamp) row[20]).toLocalDateTime() : null)
                .build();
    }
    
    /**
     * Mapea con mensaje personalizado (para CREATE/UPDATE)
     */
    public AtencionMedicaResponseDTO toDto(Object[] row, String mensaje) {
        AtencionMedicaResponseDTO dto = toDto(row);
        dto.setMensaje(mensaje);
        return dto;
    }
}
