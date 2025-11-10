package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.ClienteRequestDTO;
import com.vet.manadawoof.dtos.response.ClienteResponseDTO;
import com.vet.manadawoof.mapper.ClienteMapper;
import com.vet.manadawoof.service.ClienteService;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;


import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {
    
    @PersistenceContext
    private final EntityManager entityManager;
    private final ClienteMapper mapper;
    
    // REGISTRAR CLIENTE (SP)
    @Override
    @Transactional
    public ClienteResponseDTO registrar(ClienteRequestDTO dto) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("registrar_cliente");
        
        // Entradas
        sp.registerStoredProcedureParameter("p_id_tipo_persona_juridica", Integer.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_nombre", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_sexo", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_documento", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_tipo_documento", Integer.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_correo", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_telefono", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_direccion", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_ciudad", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_distrito", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_representante", String.class, ParameterMode.IN);
        
        // Salidas
        sp.registerStoredProcedureParameter("p_id_entidad", Long.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_id_cliente", Long.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_codigo_entidad", String.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_codigo_cliente", String.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
        
        // Set parámetros
        sp.setParameter("p_id_tipo_persona_juridica", dto.getIdTipoPersonaJuridica());
        sp.setParameter("p_nombre", dto.getNombre());
        sp.setParameter("p_sexo", dto.getSexo() != null && ! dto.getSexo().isEmpty()
                ? dto.getSexo().substring(0, 1).toUpperCase() : null);
        sp.setParameter("p_documento", dto.getDocumento());
        sp.setParameter("p_id_tipo_documento", dto.getIdTipoDocumento());
        sp.setParameter("p_correo", dto.getCorreo());
        sp.setParameter("p_telefono", dto.getTelefono());
        sp.setParameter("p_direccion", dto.getDireccion());
        sp.setParameter("p_ciudad", dto.getCiudad());
        sp.setParameter("p_distrito", dto.getDistrito());
        sp.setParameter("p_representante", dto.getRepresentante());
        
        sp.execute();
        
        // Obtener salidas
        Long idCliente = (Long) sp.getOutputParameterValue("p_id_cliente");
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        
        // --- NUEVO: CONSULTA COMPLETA DEL CLIENTE + ENTIDAD ---
        ClienteResponseDTO response = obtenerPorId(idCliente);
        if(response != null) {
            response.setMensaje(mensaje);
        } else {
            // fallback mínimo
            response = new ClienteResponseDTO();
            response.setId(idCliente);
            response.setMensaje(mensaje);
        }
        
        return response;
    }
    
    
    // ACTUALIZAR CLIENTE (SP)
    @Override
    @Transactional
    public ClienteResponseDTO actualizar(Long id, ClienteRequestDTO dto) {
        // Obtener cliente existente para conocer id_entidad
        ClienteResponseDTO existente = obtenerPorId(id);
        if(existente == null)
            throw new IllegalArgumentException("Cliente no encontrado");
        
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("actualizar_cliente");
        
        // Entradas
        sp.registerStoredProcedureParameter("p_id_entidad", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_tipo_persona_juridica", Integer.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_nombre", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_sexo", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_documento", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_tipo_documento", Integer.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_correo", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_telefono", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_direccion", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_ciudad", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_distrito", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_representante", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_activo", Boolean.class, ParameterMode.IN);
        
        // Salida
        sp.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
        
        // Set parámetros
        sp.setParameter("p_id_entidad", existente.getIdEntidad());
        sp.setParameter("p_id_tipo_persona_juridica", dto.getIdTipoPersonaJuridica());
        sp.setParameter("p_nombre", dto.getNombre());
        sp.setParameter("p_sexo", dto.getSexo());
        sp.setParameter("p_documento", dto.getDocumento());
        sp.setParameter("p_id_tipo_documento", dto.getIdTipoDocumento());
        sp.setParameter("p_correo", dto.getCorreo());
        sp.setParameter("p_telefono", dto.getTelefono());
        sp.setParameter("p_direccion", dto.getDireccion());
        sp.setParameter("p_ciudad", dto.getCiudad());
        sp.setParameter("p_distrito", dto.getDistrito());
        sp.setParameter("p_representante", dto.getRepresentante());
        sp.setParameter("p_activo", dto.getActivo() != null ? dto.getActivo() : true);
        
        sp.execute();
        
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        
        ClienteResponseDTO response = obtenerPorId(id);
        response.setMensaje(mensaje);
        return response;
    }
    
    // LISTAR CLIENTES
    @Override
    @Transactional
    public List<ClienteResponseDTO> listar() {
        List<Object[]> results = entityManager.createNativeQuery("""
                SELECT
                    c.id, c.codigo, c.activo, c.fecha_registro,
                    e.id, e.codigo, e.nombre, e.sexo, e.documento,
                    e.id_tipo_persona_juridica, e.id_tipo_documento, e.correo,
                    e.telefono, e.direccion, e.ciudad, e.distrito, e.representante,
                    e.activo, e.fecha_registro
                FROM clientes c
                JOIN entidades e ON e.id = c.id_entidad
                ORDER BY e.nombre ASC
                """).getResultList();
        
        return results.stream()
                .map(mapper :: toFullDto)
                .collect(Collectors.toList());
    }
    
    // OBTENER POR ID
    @Override
    @Transactional
    public ClienteResponseDTO obtenerPorId(Long idCliente) {
        List<Object[]> results = entityManager.createNativeQuery("""
                        SELECT
                            c.id, c.codigo, c.activo, c.fecha_registro,
                            e.id, e.codigo, e.nombre, e.sexo, e.documento,
                            e.id_tipo_persona_juridica, e.id_tipo_documento, e.correo,
                            e.telefono, e.direccion, e.ciudad, e.distrito, e.representante,
                            e.activo, e.fecha_registro
                        FROM clientes c
                        JOIN entidades e ON e.id = c.id_entidad
                        WHERE c.id = :idCliente
                        """)
                .setParameter("idCliente", idCliente)
                .getResultList();
        
        if(results.isEmpty()) return null;
        return mapper.toFullDto(results.get(0));
    }
    
    // ELIMINAR (Desactivar)
    @Override
    @Transactional
    public ClienteResponseDTO eliminar(Long idCliente) {
        // Desactivar cliente
        int updated = entityManager.createNativeQuery(
                        "UPDATE clientes SET activo = 0 WHERE id = :idCliente")
                .setParameter("idCliente", idCliente)
                .executeUpdate();
        
        if(updated == 0) {
            // No se encontró el cliente
            return null;
        }
        
        // Limpiar caché de JPA para que el select traiga el valor actualizado
        entityManager.flush();
        entityManager.clear();
        
        // Obtener cliente actualizado usando el mismo query de listar/obtenerPorId
        List<Object[]> results = entityManager.createNativeQuery("""
                        SELECT
                            c.id, c.codigo, c.activo, c.fecha_registro,
                            e.id, e.codigo, e.nombre, e.sexo, e.documento,
                            e.id_tipo_persona_juridica, e.id_tipo_documento, e.correo,
                            e.telefono, e.direccion, e.ciudad, e.distrito, e.representante,
                            e.activo, e.fecha_registro
                        FROM clientes c
                        JOIN entidades e ON e.id = c.id_entidad
                        WHERE c.id = :idCliente
                        """)
                .setParameter("idCliente", idCliente)
                .getResultList();
        
        if(results.isEmpty()) return null;
        
        // Mapper convierte correctamente activo = 0 a false
        return mapper.toFullDto(results.get(0));
    }
    
    
}
