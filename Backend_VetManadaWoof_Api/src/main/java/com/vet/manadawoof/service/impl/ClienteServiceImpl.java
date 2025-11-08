package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.ClienteRequestDTO;
import com.vet.manadawoof.dtos.response.ClienteResponseDTO;
import com.vet.manadawoof.entity.*;
import com.vet.manadawoof.mapper.ClienteMapper;
import com.vet.manadawoof.service.ClienteService;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {
    
    @PersistenceContext
    private final EntityManager entityManager; // EntityManager para SPs y consultas nativas
    
    private final ClienteMapper clienteMapper; // Mapper para convertir filas a DTOs
    
    // ---------------- REGISTRAR CLIENTE ----------------
    @Override
    @Transactional
    public ClienteResponseDTO registrar(ClienteRequestDTO dto) {
        // Construir y ejecutar SP de registro
        StoredProcedureQuery sp = buildSP(dto, "CREATE");
        sp.setParameter("p_sexo", dto.getSexo() != null ? dto.getSexo().substring(0, 1) : null);
        sp.execute();
        
        // Obtener valores de salida del SP
        String codigoEntidad = (String) sp.getOutputParameterValue("p_codigo_entidad");
        String codigoCliente = (String) sp.getOutputParameterValue("p_codigo_cliente");
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        
        // Si SP devuelve error, retornar mensaje
        if(mensaje != null && mensaje.startsWith("ERROR")) {
            return ClienteResponseDTO.builder().mensaje(mensaje).build();
        }
        
        // Consultar la entidad creada
        Object[] entRow = (Object[]) entityManager.createNativeQuery(
                        "SELECT id, codigo, nombre, sexo, documento, id_tipo_persona_juridica, " +
                                "id_tipo_documento, correo, telefono, direccion, ciudad, distrito, " +
                                "representante, activo, fecha_registro FROM entidades WHERE codigo = ?1")
                .setParameter(1, codigoEntidad)
                .getSingleResult();
        
        Long idEntidad = ((Number) entRow[0]).longValue();
        
        // Consultar cliente asociado a la entidad
        Object[] cliRow = (Object[]) entityManager.createNativeQuery(
                        "SELECT id, codigo, activo, fecha_registro FROM clientes WHERE id_entidad = ?1")
                .setParameter(1, idEntidad)
                .getSingleResult();
        
        // Convertir filas a DTO usando mapper
        return clienteMapper.toDto(entRow, cliRow, mensaje);
    }
    
    // ---------------- ACTUALIZAR CLIENTE ----------------
    @Override
    @Transactional
    public ClienteResponseDTO actualizar(Long id, ClienteRequestDTO request) {
        // Buscar cliente por id
        ClienteEntity entity = entityManager.find(ClienteEntity.class, id);
        if(entity == null) throw new RuntimeException("ERROR: Cliente no encontrado.");
        
        // Buscar entidad asociada
        EntidadEntity entidad = entityManager.find(EntidadEntity.class, entity.getEntidad().getId());
        if(entidad == null) throw new RuntimeException("ERROR: Entidad asociada no encontrada.");
        
        // Mapear campos simples
        entidad.setNombre(request.getNombre());
        entidad.setDocumento(request.getDocumento());
        entidad.setCorreo(request.getCorreo());
        entidad.setTelefono(request.getTelefono());
        entidad.setDireccion(request.getDireccion());
        entidad.setCiudad(request.getCiudad());
        entidad.setDistrito(request.getDistrito());
        entidad.setRepresentante(request.getRepresentante());
        
        // Actualizar tipo de persona y documento si vienen
        if(request.getIdTipoPersonaJuridica() != null) {
            TipoPersonaJuridicaEntity tipoPersona = entityManager.find(
                    TipoPersonaJuridicaEntity.class, request.getIdTipoPersonaJuridica());
            entidad.setTipoPersonaJuridica(tipoPersona);
        }
        if(request.getIdTipoDocumento() != null) {
            TipoDocumentoEntity tipoDoc = entityManager.find(
                    TipoDocumentoEntity.class, request.getIdTipoDocumento());
            entidad.setTipoDocumento(tipoDoc);
        }
        
        // Sexo: tomamos solo primer caracter si viene
        if(request.getSexo() != null && ! request.getSexo().isEmpty()) {
            entidad.setSexo(request.getSexo().substring(0, 1));
        }
        
        // Guardar cambios
        entityManager.merge(entidad);  // merge de entidad
        entityManager.merge(entity);   // merge de cliente (aunque no se cambie nada en cliente)
        
        // Retornar DTO actualizado
        return clienteMapper.toDto(
                new Object[] {
                        entidad.getId(), entidad.getCodigo(), entidad.getNombre(), entidad.getSexo(),
                        entidad.getDocumento(), entidad.getIdTipoPersonaJuridica(), entidad.getIdTipoDocumento(),
                        entidad.getCorreo(), entidad.getTelefono(), entidad.getDireccion(), entidad.getCiudad(),
                        entidad.getDistrito(), entidad.getRepresentante(), entidad.getActivo() ? 1 : 0, entidad.getFechaRegistro()},
                new Object[] {entity.getId(), entity.getCodigo(), entity.getActivo() ? 1 : 0, entity.getFechaRegistro()},
                "Actualización exitosa");
    }
    
    // ---------------- LISTAR CLIENTES ----------------
    @Override
    @Transactional
    public List<ClienteResponseDTO> listar() {
        // Listar todos los clientes con datos de entidad
        List<Object[]> results = entityManager.createNativeQuery(
                        "SELECT c.id, c.codigo, c.activo, e.id, e.codigo, e.nombre, e.sexo, e.documento, " +
                                "e.id_tipo_persona_juridica, e.id_tipo_documento, e.correo, e.telefono, " +
                                "e.direccion, e.ciudad, e.distrito, e.representante, e.activo, e.fecha_registro " +
                                "FROM clientes c JOIN entidades e ON c.id_entidad = e.id")
                .getResultList();
        
        // Mapear cada fila a DTO
        return results.stream()
                .map(clienteMapper :: toFullDto)
                .collect(Collectors.toList());
    }
    
    // ---------------- OBTENER CLIENTE POR ID ----------------
    @Override
    @Transactional
    public ClienteResponseDTO obtenerPorId(Long id) {
        Object[] row = (Object[]) entityManager.createNativeQuery(
                        "SELECT c.id, c.codigo, c.activo, e.id, e.codigo, e.nombre, e.sexo, e.documento, " +
                                "e.id_tipo_persona_juridica, e.id_tipo_documento, e.correo, e.telefono, " +
                                "e.direccion, e.ciudad, e.distrito, e.representante, e.activo, e.fecha_registro " +
                                "FROM clientes c JOIN entidades e ON c.id_entidad = e.id WHERE c.id = ?1")
                .setParameter(1, id)
                .getSingleResult();
        
        return row != null ? clienteMapper.toFullDto(row) : null;
    }
    
    // ---------------- ELIMINAR CLIENTE (LÓGICAMENTE) ----------------
    @Override
    @Transactional
    public ClienteResponseDTO eliminar(Long idCliente) {
        Object[] cliRow = (Object[]) entityManager.createNativeQuery(
                        "SELECT id, codigo, id_entidad FROM clientes WHERE id = ?1")
                .setParameter(1, idCliente)
                .getSingleResult();
        
        if(cliRow == null) {
            return ClienteResponseDTO.builder().mensaje("ERROR: Cliente no encontrado").build();
        }
        
        Long idEntidad = ((Number) cliRow[2]).longValue();
        
        // Actualización lógica del cliente
        entityManager.createNativeQuery("UPDATE clientes SET activo = 0 WHERE id = ?1")
                .setParameter(1, idCliente)
                .executeUpdate();
        
        // Consultar datos de entidad para el DTO
        Object[] entRow = (Object[]) entityManager.createNativeQuery(
                        "SELECT id, codigo, nombre, sexo, documento, id_tipo_persona_juridica, " +
                                "id_tipo_documento, correo, telefono, direccion, ciudad, distrito, " +
                                "representante, activo, fecha_registro FROM entidades WHERE id = ?1")
                .setParameter(1, idEntidad)
                .getSingleResult();
        
        ClienteResponseDTO dto = clienteMapper.toDto(entRow, cliRow, "Cliente eliminado lógicamente con éxito");
        dto.setActivo(false); // Marcar DTO como inactivo
        return dto;
    }
    
    // ---------------- CONSTRUIR SP CREATE / UPDATE ----------------
    private StoredProcedureQuery buildSP(ClienteRequestDTO dto, String accion) {
        StoredProcedureQuery sp;
        
        if("CREATE".equalsIgnoreCase(accion)) {
            // SP para crear cliente
            sp = entityManager.createStoredProcedureQuery("registrar_cliente");
            
            // Registrar parámetros de entrada y salida
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
            sp.registerStoredProcedureParameter("p_codigo_entidad", String.class, ParameterMode.OUT);
            sp.registerStoredProcedureParameter("p_codigo_cliente", String.class, ParameterMode.OUT);
            sp.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
            
            // Asignar parámetros de entrada
            sp.setParameter("p_id_tipo_persona_juridica", dto.getIdTipoPersonaJuridica());
            sp.setParameter("p_nombre", dto.getNombre());
            sp.setParameter("p_sexo", dto.getSexo() != null ? dto.getSexo().substring(0, 1) : null);
            sp.setParameter("p_documento", dto.getDocumento());
            sp.setParameter("p_id_tipo_documento", dto.getIdTipoDocumento());
            sp.setParameter("p_correo", dto.getCorreo());
            sp.setParameter("p_telefono", dto.getTelefono());
            sp.setParameter("p_direccion", dto.getDireccion());
            sp.setParameter("p_ciudad", dto.getCiudad());
            sp.setParameter("p_distrito", dto.getDistrito());
            
        } else {
            // SP para actualizar cliente
            sp = entityManager.createStoredProcedureQuery("actualizar_cliente");
            
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
            sp.registerStoredProcedureParameter("p_activo", Boolean.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
            
            sp.setParameter("p_id_entidad", dto.getIdEntidad());
            sp.setParameter("p_id_tipo_persona_juridica", dto.getIdTipoPersonaJuridica());
            sp.setParameter("p_nombre", dto.getNombre());
            sp.setParameter("p_sexo", dto.getSexo() != null ? dto.getSexo().substring(0, 1) : null);
            sp.setParameter("p_documento", dto.getDocumento());
            sp.setParameter("p_id_tipo_documento", dto.getIdTipoDocumento());
            sp.setParameter("p_correo", dto.getCorreo());
            sp.setParameter("p_telefono", dto.getTelefono());
            sp.setParameter("p_direccion", dto.getDireccion());
            sp.setParameter("p_ciudad", dto.getCiudad());
            sp.setParameter("p_distrito", dto.getDistrito());
            sp.setParameter("p_activo", dto.getActivo());
        }
        return sp;
    }
}
