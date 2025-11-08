package com.vet.manadawoof.mapper;

import com.vet.manadawoof.dtos.request.ColaboradorRequestDTO;
import com.vet.manadawoof.dtos.response.ColaboradorResponseDTO;
import com.vet.manadawoof.entity.ColaboradorEntity;
import com.vet.manadawoof.entity.EntidadEntity;
import com.vet.manadawoof.entity.UsuarioEntity;
import org.springframework.stereotype.Component;

import java.sql.Date;


// Mapper de Colaborador
// Centraliza la conversión entre:
// Resultados SQL nativos (Object[]) DTOs de solicitud y respuesta - Entidades JPA
@Component
public class ColaboradorMapper {
    // Convierte una fila combinada (JOIN entidades + colaboradores)
    // obtenida de consultas nativas en un DTO de respuesta completo.
    // Usado principalmente en registrar() y actualizar().
    public ColaboradorResponseDTO toDto(Object[] entRow, Object[] colRow, String mensaje) {
        Long idEntidad = ((Number) entRow[0]).longValue();
        Integer idUsuario = colRow[2] != null ? ((Number) colRow[2]).intValue() : null;
        
        return ColaboradorResponseDTO.builder().id(((Number) colRow[0]).longValue()) // id del colaborador
                .codigoColaborador((String) colRow[1])
                .idEntidad(idEntidad).
                usuario(idUsuario).
                nombre((String) entRow[2])
                .sexo(entRow[3] != null ? entRow[3].toString() : null)
                .documento((String) entRow[4])
                .idTipoPersonaJuridica(entRow[5] != null ? ((Number) entRow[5]).intValue() : null)
                .idTipoDocumento(entRow[6] != null ? ((Number) entRow[6]).intValue() : null)
                .correo((String) entRow[7])
                .telefono((String) entRow[8])
                .direccion((String) entRow[9])
                .ciudad((String) entRow[10])
                .distrito((String) entRow[11])
                .activo(colRow[5] != null ? ((Number) colRow[5]).intValue() == 1 : true)
                .fechaIngreso(colRow[3] != null ? ((Date) colRow[3]).toLocalDate() : null)
                .foto((String) colRow[4]).mensaje(mensaje)
                .build();
    }
    
    /**
     * ===============================================================
     * Convierte un DTO de solicitud (ColaboradorRequestDTO)
     * a una entidad JPA (ColaboradorEntity), preparando las relaciones
     * sin necesidad de hacer consultas adicionales.
     * ===============================================================
     */
    public ColaboradorEntity toEntity(ColaboradorRequestDTO dto) {
        ColaboradorEntity entity = new ColaboradorEntity();
        
        // --- Relación con Entidad ---
        if(dto.getIdEntidad() != null) {
            EntidadEntity entidad = new EntidadEntity(); entidad.setId(dto.getIdEntidad()); entity.setEntidad(entidad);
        }
        
        // --- Relación con Usuario ---
        if(dto.getIdUsuario() != null) {
            UsuarioEntity usuario = new UsuarioEntity(); usuario.setId(dto.getIdUsuario()); entity.setUsuario(usuario);
        }
        
        // Conversión de LocalDate a Date
        if(dto.getFechaIngreso() != null) {
            entity.setFechaIngreso(Date.valueOf(dto.getFechaIngreso()));
        }
        
        // Asignar campos simples
        entity.setFoto(dto.getFoto()); entity.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
        
        return entity;
    }
    
    // Convierte una fila combinada simple (JOIN colaboradores + entidades)
    // usada en listados u obtención individual.
    public ColaboradorResponseDTO toFullDto(Object[] row) {
        return ColaboradorResponseDTO.builder().id(((Number) row[0]).longValue()) // c.id
                .codigoColaborador((String) row[1])
                .idEntidad(((Number) row[2]).longValue())
                .fechaIngreso(row[3] != null ? ((Date) row[3]).toLocalDate() : null)
                .activo(row[5] != null && ((Number) row[5]).intValue() == 1)
                .foto((String) row[6])
                .nombre((String) row[7])
                .sexo(row[8] != null ? row[8].toString() : null)
                .documento((String) row[9])
                .idTipoPersonaJuridica(row[10] != null ? ((Number) row[10]).intValue() : null)
                .idTipoDocumento(row[11] != null ? ((Number) row[11]).intValue() : null)
                .correo((String) row[12]).telefono((String) row[13])
                .direccion((String) row[14])
                .ciudad((String) row[15])
                .distrito((String) row[16])
                .build();
    }
    
    // Variante extendida del mapeo completo (entidad + colaborador)
    // usada internamente por registrar() y actualizar().
    
    public ColaboradorResponseDTO toFullDto(Object[] entRow, Object[] colRow, String mensaje) {
        return toDto(entRow, colRow, mensaje);
    }
}
