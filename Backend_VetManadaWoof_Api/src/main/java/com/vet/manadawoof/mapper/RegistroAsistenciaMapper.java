package com.vet.manadawoof.mapper;

import com.vet.manadawoof.dtos.response.RegistroAsistenciaResponseDTO;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.time.LocalTime;

@Component
public class RegistroAsistenciaMapper {
    
    
    public RegistroAsistenciaResponseDTO toResponseDTO(Object[] row) {
        return RegistroAsistenciaResponseDTO.builder().idColaborador(row[0] != null ? ((Number) row[0]).longValue() : null).colaborador((String) row[1]).fecha(row[2] != null ? ((java.sql.Date) row[2]).toLocalDate() : null).diaSemana((String) row[3]).horario((String) row[4]).horaEntrada(toLocalTime(row[5])).horaLunchInicio(toLocalTime(row[6])).horaLunchFin(toLocalTime(row[7])).horaSalida(toLocalTime(row[8])).minutosTrabajados(toInteger(row[9])).minutosLunch(toInteger(row[10])).tardanzaMinutos(toInteger(row[11])).estadoAsistencia((String) row[12]).horaProgramadaInicio(toLocalTime(row[13])).horaProgramadaFin(toLocalTime(row[14])).observaciones((String) row[15]).build();
    }
    
    /**
     * Convierte java.sql.Time a LocalTime
     */
    private LocalTime toLocalTime(Object obj) {
        if(obj == null) return null; if(obj instanceof Time) {
            return ((Time) obj).toLocalTime();
        } return null;
    }
    
    /**
     * Convierte Number a Integer de forma segura
     */
    private Integer toInteger(Object obj) {
        if(obj == null) return null; if(obj instanceof Number) {
            return ((Number) obj).intValue();
        } return null;
    }
}
