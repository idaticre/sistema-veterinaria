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
        Long idEntidad = ((Number) entRow[0]).longValue();
        
        return ClienteResponseDTO.builder()
                // Datos cliente
                .id(((Number) cliRow[0]).longValue())
                .codigoCliente((String) cliRow[1])
                .activo(cliRow[2] != null ? ((Number) cliRow[2]).intValue() == 1 : true)
                .fechaRegistro(parseFecha(cliRow[3]))
                
                // Datos entidad
                .idEntidad(idEntidad)
                .nombre((String) entRow[2])
                .sexo(entRow[3] != null ? entRow[3].toString() : null)
                .documento((String) entRow[4])
                .idTipoPersonaJuridica(entRow[5] != null ? ((Number) entRow[5]).intValue() : null)
                .idTipoDocumento(entRow[6] != null ? ((Number) entRow[6]).intValue() : null)
                .correo((String) entRow[7])
                .telefono((String) entRow[8])
                .direccion((String) entRow[9])
                .ciudad((String) entRow[10])
                .distrito((String) entRow[11])
                .representante((String) entRow[12])
                .mensaje(mensaje)
                .build();
    }
    
    // ---------------- CONVERTIR DTO A ENTIDAD ----------------
    public ClienteEntity toEntity(ClienteRequestDTO dto) {
        ClienteEntity entity = new ClienteEntity();
        
        // Relación con Entidad
        if(dto.getIdEntidad() != null) {
            EntidadEntity entidad = new EntidadEntity();
            entidad.setId(dto.getIdEntidad());
            entity.setEntidad(entidad);
        }
        
        // Asignar campos simples
        entity.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
        
        return entity;
    }
    
    // ---------------- CONVERTIR FILAS COMPLETAS (JOIN) A DTO ----------------
    public ClienteResponseDTO toFullDto(Object[] row) {
        return ClienteResponseDTO.builder()
                .id(row[0] != null ? ((Number) row[0]).longValue() : null)
                .codigoCliente(row[1] != null ? row[1].toString() : null)
                .activo(row[2] != null ? ((Number) row[2]).intValue() == 1 : true)
                .idEntidad(row[3] != null ? ((Number) row[3]).longValue() : null)
                .nombre(row[5] != null ? row[5].toString() : null)
                .sexo(row[6] != null ? row[6].toString() : null)
                .documento(row[7] != null ? row[7].toString() : null)
                .idTipoPersonaJuridica(row[8] != null ? ((Number) row[8]).intValue() : null)
                .idTipoDocumento(row[9] != null ? ((Number) row[9]).intValue() : null)
                .correo(row[10] != null ? row[10].toString() : null)
                .telefono(row[11] != null ? row[11].toString() : null)
                .direccion(row[12] != null ? row[12].toString() : null)
                .ciudad(row[13] != null ? row[13].toString() : null)
                .distrito(row[14] != null ? row[14].toString() : null)
                .representante(row[15] != null ? row[15].toString() : null)
                .fechaRegistro(parseFecha(row[17]))
                .mensaje("Operación exitosa")
                .build();
    }
    
    // Variante extendida del mapeo completo (entidad + cliente)
    public ClienteResponseDTO toFullDto(Object[] entRow, Object[] cliRow, String mensaje) {
        return toDto(entRow, cliRow, mensaje);
    }
}
