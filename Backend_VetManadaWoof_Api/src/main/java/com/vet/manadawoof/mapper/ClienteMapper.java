package com.vet.manadawoof.mapper;

import com.vet.manadawoof.dtos.request.ClienteRequestDTO;
import com.vet.manadawoof.dtos.response.ClienteResponseDTO;
import com.vet.manadawoof.entity.ClienteEntity;
import com.vet.manadawoof.entity.EntidadEntity;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
public class ClienteMapper {
    
    // ---------------- UTILIDAD PARA FECHAS ----------------
    private LocalDateTime parseFecha(Object obj) {
        if(obj == null) return null;
        if(obj instanceof Timestamp) return ((Timestamp) obj).toLocalDateTime();
        if(obj instanceof LocalDateTime) return (LocalDateTime) obj;
        return null;
    }
    
    // ---------------- CONVERTIR FILAS SQL A DTO ----------------
    public ClienteResponseDTO toDto(Object[] entRow, Object[] cliRow, String mensaje) {
        if(entRow == null || cliRow == null) return null;
        
        Long idEntidad = toLong(entRow[0]);
        
        return ClienteResponseDTO.builder()
                // Datos cliente
                .id(toLong(cliRow[0]))
                .codigoCliente(cliRow[1] != null ? cliRow[1].toString() : null)
                .activo(cliRow[2] != null ? toBoolean(cliRow[2]) : true)
                .fechaRegistro(parseFecha(cliRow[3]))
                
                // Datos entidad
                .idEntidad(idEntidad)
                .nombre(entRow[2] != null ? entRow[2].toString() : null)
                .sexo(entRow[3] != null ? entRow[3].toString() : null)
                .documento(entRow[4] != null ? entRow[4].toString() : null)
                .idTipoPersonaJuridica(entRow[5] != null ? toInt(entRow[5]) : null)
                .idTipoDocumento(entRow[6] != null ? toInt(entRow[6]) : null)
                .correo(entRow[7] != null ? entRow[7].toString() : null)
                .telefono(entRow[8] != null ? entRow[8].toString() : null)
                .direccion(entRow[9] != null ? entRow[9].toString() : null)
                .ciudad(entRow[10] != null ? entRow[10].toString() : null)
                .distrito(entRow[11] != null ? entRow[11].toString() : null)
                .representante(entRow[12] != null ? entRow[12].toString() : null)
                .activo(entRow[13] != null ? toBoolean(entRow[13]) : true)
                .fechaRegistroEntidad(parseFecha(entRow[14]))
                .mensaje(mensaje)
                .build();
    }
    
    // ---------------- CONVERTIR DTO A ENTIDAD ----------------
    public ClienteEntity toEntity(ClienteRequestDTO dto) {
        ClienteEntity entity = new ClienteEntity();
        
        if(dto.getIdEntidad() != null) {
            EntidadEntity entidad = new EntidadEntity();
            entidad.setId(dto.getIdEntidad());
            entity.setEntidad(entidad);
        }
        
        entity.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
        
        return entity;
    }
    
    // ---------------- CONVERTIR FILAS COMPLETAS (JOIN) A DTO ----------------
    public ClienteResponseDTO toFullDto(Object[] row) {
        if(row == null) return null;
        
        return ClienteResponseDTO.builder()
                .id(toLong(row[0]))                           // c.id
                .codigoCliente(row[1] != null ? row[1].toString() : null)
                .activo(toBoolean(row[2]))
                .fechaRegistro(parseFecha(row[3]))
                .idEntidad(toLong(row[4]))                     // e.id
                .nombre(row[6] != null ? row[6].toString() : null)
                .sexo(row[7] != null ? row[7].toString() : null)
                .documento(row[8] != null ? row[8].toString() : null)
                .idTipoPersonaJuridica(toInt(row[9]))
                .idTipoDocumento(toInt(row[10]))
                .correo(row[11] != null ? row[11].toString() : null)
                .telefono(row[12] != null ? row[12].toString() : null)
                .direccion(row[13] != null ? row[13].toString() : null)
                .ciudad(row[14] != null ? row[14].toString() : null)
                .distrito(row[15] != null ? row[15].toString() : null)
                .representante(row[16] != null ? row[16].toString() : null)
                .fechaRegistroEntidad(parseFecha(row[18]))
                .mensaje("Operación exitosa")
                .build();
    }
    
    // Variante extendida para SP + mensaje
    public ClienteResponseDTO toFullDto(Object[] entRow, Object[] cliRow, String mensaje) {
        return toDto(entRow, cliRow, mensaje);
    }
    
    // ---------------- MÉTODOS DE CONVERSIÓN SEGURA ----------------
    private Long toLong(Object value) {
        if(value == null) return null;
        if(value instanceof Number) return ((Number) value).longValue();
        if(value instanceof String s && s.matches("\\d+")) return Long.parseLong(s);
        return null;
    }
    
    private Integer toInt(Object value) {
        if(value == null) return null;
        if(value instanceof Number) return ((Number) value).intValue();
        if(value instanceof String s && s.matches("\\d+")) return Integer.parseInt(s);
        return null;
    }
    
    private Boolean toBoolean(Object value) {
        if(value == null) return true; // activo por defecto
        if(value instanceof Boolean) return (Boolean) value;
        if(value instanceof Number) return ((Number) value).intValue() == 1;
        if(value instanceof String s) return s.equals("1") || s.equalsIgnoreCase("true");
        return true;
    }
}
