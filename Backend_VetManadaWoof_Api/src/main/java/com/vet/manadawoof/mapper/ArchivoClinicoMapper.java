package com.vet.manadawoof.mapper;

import com.vet.manadawoof.dtos.response.ArchivoClinicoResponseDTO;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
public class ArchivoClinicoMapper {
    
    /**
     * Mapea una fila de resultado SQL a ArchivoClinicoResponseDTO
     * Usado en subirArchivo() y consultas
     */
    public ArchivoClinicoResponseDTO toDto(Object[] row) {
        return ArchivoClinicoResponseDTO.builder()
                .id(((Number) row[0]).longValue())
                .codigo((String) row[1])
                .idRegistroAtencion(((Number) row[2]).longValue())
                .idTipoArchivo(row[3] != null ? ((Number) row[3]).intValue() : null)
                .nombreArchivo((String) row[4])
                .extensionArchivo((String) row[5])
                .descripcion((String) row[6])
                .fechaSubida(row[7] != null ? ((Timestamp) row[7]).toLocalDateTime() : null)
                .build();
    }
    
    /**
     * Mapea con mensaje personalizado (para CREATE)
     */
    public ArchivoClinicoResponseDTO toDto(Object[] row, String mensaje) {
        ArchivoClinicoResponseDTO dto = toDto(row);
        dto.setMensaje(mensaje);
        return dto;
    }
}
