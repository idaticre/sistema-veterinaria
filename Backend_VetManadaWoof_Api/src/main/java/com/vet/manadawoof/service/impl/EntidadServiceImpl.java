package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.EntidadRequestDTO;
import com.vet.manadawoof.dtos.response.EntidadResponseDTO;
import com.vet.manadawoof.service.EntidadService;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EntidadServiceImpl implements EntidadService {
    
    private final EntityManager entityManager;
    
    @Override
    @Transactional
    public EntidadResponseDTO crearEntidad(EntidadRequestDTO request) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("registrar_entidad_base")
                .registerStoredProcedureParameter("p_id_tipo_persona_juridica", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_nombre", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_sexo", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_documento", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_id_tipo_documento", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_correo", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_telefono", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_direccion", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_ciudad", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_distrito", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_representante", String.class, ParameterMode.IN)
                // SP sigue usando INT, aquí convertimos Long -> Integer
                .registerStoredProcedureParameter("p_id_entidad", Integer.class, ParameterMode.OUT)
                .registerStoredProcedureParameter("p_codigo_entidad", String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
        
        sp.setParameter("p_id_tipo_persona_juridica", request.getIdTipoPersonaJuridica());
        sp.setParameter("p_nombre", request.getNombre());
        sp.setParameter("p_sexo", request.getSexo() != null ? request.getSexo().substring(0, 1) : null);
        sp.setParameter("p_documento", request.getDocumento());
        sp.setParameter("p_id_tipo_documento", request.getIdTipoDocumento());
        sp.setParameter("p_correo", request.getCorreo());
        sp.setParameter("p_telefono", request.getTelefono());
        sp.setParameter("p_direccion", request.getDireccion());
        sp.setParameter("p_ciudad", request.getCiudad());
        sp.setParameter("p_distrito", request.getDistrito());
        sp.setParameter("p_representante", request.getRepresentante());
        
        sp.execute();
        
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        if(mensaje != null && mensaje.startsWith("ERROR:"))
            throw new RuntimeException(mensaje);
        
        // Convertimos el OUT INT to Long
        Long id = ((Number) sp.getOutputParameterValue("p_id_entidad")).longValue();
        String codigo = (String) sp.getOutputParameterValue("p_codigo_entidad");
        
        return EntidadResponseDTO.builder()
                .id(id)
                .codigo(codigo)
                .nombre(request.getNombre())
                .correo(request.getCorreo())
                .telefono(request.getTelefono())
                .sexo(request.getSexo())
                .documento(request.getDocumento())
                .direccion(request.getDireccion())
                .ciudad(request.getCiudad())
                .distrito(request.getDistrito())
                .representante(request.getRepresentante())
                .activo(true)
                .idTipoDocumento(request.getIdTipoDocumento())
                .idTipoPersonaJuridica(request.getIdTipoPersonaJuridica())
                .build();
    }
    
    @Override
    @Transactional
    public EntidadResponseDTO actualizarEntidad(EntidadRequestDTO request) {
        if(request.getId() == null)
            throw new RuntimeException("ERROR: id es requerido para actualizar.");
        
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("actualizar_entidad_base")
                // Convertimos Long -> Integer al pasar al SP
                .registerStoredProcedureParameter("p_id_entidad", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_id_tipo_persona_juridica", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_nombre", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_sexo", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_documento", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_id_tipo_documento", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_correo", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_telefono", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_direccion", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_ciudad", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_distrito", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_representante", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_activo", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
        
        sp.setParameter("p_id_entidad", request.getId().intValue()); // <--- Conversión Long -> Integer
        sp.setParameter("p_id_tipo_persona_juridica", request.getIdTipoPersonaJuridica());
        sp.setParameter("p_nombre", request.getNombre());
        sp.setParameter("p_sexo", request.getSexo() != null ? request.getSexo().substring(0, 1) : null);
        sp.setParameter("p_documento", request.getDocumento());
        sp.setParameter("p_id_tipo_documento", request.getIdTipoDocumento());
        sp.setParameter("p_correo", request.getCorreo());
        sp.setParameter("p_telefono", request.getTelefono());
        sp.setParameter("p_direccion", request.getDireccion());
        sp.setParameter("p_ciudad", request.getCiudad());
        sp.setParameter("p_distrito", request.getDistrito());
        sp.setParameter("p_representante", request.getRepresentante());
        // Boolean activo convertido a Integer 1/0
        sp.setParameter("p_activo", request.getActivo() != null && request.getActivo() ? 1 : 0);
        
        sp.execute();
        
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        if(mensaje != null && mensaje.startsWith("ERROR:"))
            throw new RuntimeException(mensaje);
        
        // Obtener código para DTO
        Object result = entityManager.createNativeQuery(
                        "SELECT codigo FROM entidades WHERE id = ?1")
                .setParameter(1, request.getId().intValue()) // <-- Posicional
                .getSingleResult();
        
        
        String codigo = (result != null) ? result.toString() : null;
        
        return EntidadResponseDTO.builder()
                .id(request.getId())
                .codigo(codigo)
                .nombre(request.getNombre())
                .correo(request.getCorreo())
                .telefono(request.getTelefono())
                .sexo(request.getSexo())
                .documento(request.getDocumento())
                .direccion(request.getDireccion())
                .ciudad(request.getCiudad())
                .distrito(request.getDistrito())
                .representante(request.getRepresentante())
                .activo(request.getActivo())
                .idTipoDocumento(request.getIdTipoDocumento())
                .idTipoPersonaJuridica(request.getIdTipoPersonaJuridica())
                .build();
    }
    
    @Override
    @Transactional
    public EntidadResponseDTO eliminarEntidad(Long id) {
        EntidadRequestDTO dto = new EntidadRequestDTO();
        dto.setId(id);
        dto.setActivo(false);
        return actualizarEntidad(dto);
    }
    
    @Override
    @Transactional
    public List<EntidadResponseDTO> listarEntidades() {
        List<Object[]> rows = entityManager.createNativeQuery(
                "SELECT e.id, e.codigo, e.nombre, e.correo, e.telefono, e.sexo, e.documento, " +
                        "e.direccion, e.ciudad, e.distrito, e.representante, e.activo, " +
                        "e.id_tipo_persona_juridica, tpj.nombre, " +
                        "e.id_tipo_documento, td.descripcion, " +
                        "e.fecha_registro " +
                        "FROM entidades e " +
                        "LEFT JOIN tipo_persona_juridica tpj ON e.id_tipo_persona_juridica = tpj.id " +
                        "LEFT JOIN tipo_documento td ON e.id_tipo_documento = td.id"
        ).getResultList();
        
        return rows.stream().map(r -> EntidadResponseDTO.builder()
                .id(((Number) r[0]).longValue())
                .codigo((String) r[1])
                .nombre((String) r[2])
                .correo((String) r[3])
                .telefono((String) r[4])
                .sexo(r[5] != null ? r[5].toString() : null)
                .documento((String) r[6])
                .direccion((String) r[7])
                .ciudad((String) r[8])
                .distrito((String) r[9])
                .representante((String) r[10])
                .activo(r[11] != null && (((Number) r[11]).intValue() == 1))
                .idTipoPersonaJuridica(r[12] != null ? ((Number) r[12]).intValue() : null)  // <-- ahora sí
                .tipoPersonaJuridica((String) r[13])
                .idTipoDocumento(r[14] != null ? ((Number) r[14]).intValue() : null)        // <-- ahora sí
                .tipoDocumento((String) r[15])
                .fechaRegistro((r[16] != null) ? ((java.sql.Timestamp) r[16]).toLocalDateTime() : null)
                .build()
        ).toList();
        
        
    }
}
