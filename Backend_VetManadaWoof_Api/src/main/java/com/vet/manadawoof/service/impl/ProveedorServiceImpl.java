package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.ProveedorRequestDTO;
import com.vet.manadawoof.dtos.response.ProveedorResponseDTO;
import com.vet.manadawoof.service.ProveedorService;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
// Implementación del servicio de proveedores.
// Gestiona el registro, actualización, listado, búsqueda y eliminación de proveedores
// utilizando procedimientos almacenados y consultas nativas.
public class ProveedorServiceImpl implements ProveedorService {
    
    @PersistenceContext
    // Manejador de persistencia JPA para ejecutar SPs y SQL nativo
    private final EntityManager entityManager;
    
    /**
     * Registra un nuevo proveedor en la base de datos usando el procedimiento almacenado `registrar_proveedor`.
     * Retorna un DTO con los datos generados y el mensaje de resultado.
     */
    @Override
    @Transactional
    public ProveedorResponseDTO registrar(ProveedorRequestDTO dto) {
        // Construcción del Stored Procedure para registrar proveedor
        StoredProcedureQuery sp = buildSP(dto, "CREATE");
        sp.setParameter("p_sexo", dto.getSexo() != null ? dto.getSexo() : null);
        sp.execute();
        
        // Recuperar valores de salida del SP
        String codigoEntidad = (String) sp.getOutputParameterValue("p_codigo_entidad");
        String codigoProveedor = (String) sp.getOutputParameterValue("p_codigo_proveedor");
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        
        // Validar respuesta de error
        if(mensaje != null && mensaje.startsWith("ERROR")) {
            return ProveedorResponseDTO.builder().mensaje(mensaje).build();
        }
        
        // Consultar la entidad recién creada para poblar el DTO completo
        Object[] entRow = (Object[]) entityManager.createNativeQuery(
                        "SELECT id, codigo, nombre, sexo, documento, id_tipo_persona_juridica, " +
                                "id_tipo_documento, correo, telefono, direccion, ciudad, distrito, " +
                                "representante, activo, fecha_registro FROM entidades WHERE codigo = ?1")
                .setParameter(1, codigoEntidad)
                .getSingleResult();
        
        // Obtener IDs reales vinculados
        Long idEntidad = ((Number) entityManager.createNativeQuery(
                        "SELECT id FROM entidades WHERE codigo = ?1")
                .setParameter(1, codigoEntidad)
                .getSingleResult()).longValue();
        
        Long idProveedor = ((Number) entityManager.createNativeQuery(
                        "SELECT id FROM proveedores WHERE id_entidad = ?1")
                .setParameter(1, idEntidad)
                .getSingleResult()).longValue();
        
        // Construir respuesta completa con datos fusionados
        return ProveedorResponseDTO.builder()
                .id(idProveedor)
                .idEntidad(idEntidad)
                .codigoProveedor(codigoProveedor)
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
                .activo(entRow[13] != null ? ((Number) entRow[13]).intValue() == 1 : true)
                .fechaRegistro(entRow[14] != null ? ((Timestamp) entRow[14]).toLocalDateTime() : null)
                .mensaje(mensaje)
                .build();
    }
    
    /**
     * Actualiza la información de un proveedor y su entidad vinculada.
     * Utiliza el procedimiento almacenado `actualizar_proveedor`.
     */
    @Override
    @Transactional
    public ProveedorResponseDTO actualizar(ProveedorRequestDTO dto) {
        if(dto.getId() == null)
            throw new RuntimeException("ID del proveedor requerido para actualizar");
        
        // 🔍 Obtener el id_entidad automáticamente
        Long idEntidad = ((Number) entityManager.createNativeQuery(
                        "SELECT id_entidad FROM proveedores WHERE id = ?1")
                .setParameter(1, dto.getId())
                .getSingleResult()).longValue();
        
        // Construcción del Stored Procedure para actualización
        StoredProcedureQuery sp = buildSP(dto, "UPDATE");
        sp.setParameter("p_id_entidad", idEntidad);
        sp.setParameter("p_sexo", dto.getSexo() != null ? dto.getSexo() : null);
        sp.execute();
        
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        if(mensaje != null && mensaje.startsWith("ERROR")) {
            return ProveedorResponseDTO.builder().mensaje(mensaje).build();
        }
        
        // Consultar la entidad actualizada
        Object[] entRow = (Object[]) entityManager.createNativeQuery(
                        "SELECT id, codigo, nombre, sexo, documento, id_tipo_persona_juridica, " +
                                "id_tipo_documento, correo, telefono, direccion, ciudad, distrito, " +
                                "representante, activo, fecha_registro FROM entidades WHERE id = ?1")
                .setParameter(1, idEntidad)
                .getSingleResult();
        
        String codigoProveedor = (String) entityManager.createNativeQuery(
                        "SELECT codigo FROM proveedores WHERE id_entidad = ?1")
                .setParameter(1, idEntidad)
                .getSingleResult();
        
        return ProveedorResponseDTO.builder()
                .id(dto.getId())
                .idEntidad(idEntidad)
                .codigoProveedor(codigoProveedor)
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
                .activo(entRow[13] != null ? ((Number) entRow[13]).intValue() == 1 : true)
                .fechaRegistro(entRow[14] != null ? ((Timestamp) entRow[14]).toLocalDateTime() : null)
                .mensaje(mensaje)
                .build();
    }
    
    /**
     * Lista todos los proveedores con sus datos de entidad asociados.
     * Ejecuta una consulta SQL nativa con JOIN para optimizar resultados.
     */
    @Override
    @Transactional
    public List<ProveedorResponseDTO> listar() {
        List<Object[]> results = entityManager.createNativeQuery(
                "SELECT p.id, p.codigo, p.activo, e.id, e.codigo, e.nombre, e.sexo, e.documento, " +
                        "e.id_tipo_persona_juridica, e.id_tipo_documento, e.correo, e.telefono, e.direccion, " +
                        "e.ciudad, e.distrito, e.representante, e.activo, e.fecha_registro " +
                        "FROM proveedores p JOIN entidades e ON p.id_entidad = e.id"
        ).getResultList();
        
        return results.stream()
                .map(this :: mapRowToFullDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene los datos completos de un proveedor según su ID usando un SP.
     */
    @Override
    @Transactional
    public ProveedorResponseDTO obtenerPorId(Long id) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("obtener_proveedor_por_id");
        sp.registerStoredProcedureParameter("p_id_entidad", Long.class, ParameterMode.IN);
        sp.setParameter("p_id_entidad", id);
        Object[] row = (Object[]) sp.getSingleResult();
        return row != null ? mapRowToDto(row) : null;
    }
    
    /**
     * Elimina lógicamente un proveedor (actualiza el campo `activo` a 0).
     * También devuelve información de la entidad desactivada.
     */
    @Override
    @Transactional
    public ProveedorResponseDTO eliminar(Long idProveedor) {
        // 1. Verificar existencia
        Object[] row = (Object[]) entityManager.createNativeQuery(
                        "SELECT p.id, p.codigo, p.id_entidad, p.activo FROM proveedores p WHERE p.id = ?1")
                .setParameter(1, idProveedor)
                .getSingleResult();
        
        if(row == null) {
            return ProveedorResponseDTO.builder()
                    .mensaje("ERROR: Proveedor no encontrado")
                    .build();
        }
        
        Long idEntidad = ((Number) row[2]).longValue();
        
        // 2. Marcar como inactivo
        entityManager.createNativeQuery("UPDATE proveedores SET activo = 0 WHERE id = ?1")
                .setParameter(1, idProveedor)
                .executeUpdate();
        
        // 3. Consultar datos actualizados
        Object[] entRow = (Object[]) entityManager.createNativeQuery(
                        "SELECT e.id, e.codigo, e.nombre, e.sexo, e.documento, e.id_tipo_persona_juridica, " +
                                "e.id_tipo_documento, e.correo, e.telefono, e.direccion, e.ciudad, e.distrito, " +
                                "e.representante, e.activo, e.fecha_registro FROM entidades e WHERE e.id = ?1")
                .setParameter(1, idEntidad)
                .getSingleResult();
        
        return ProveedorResponseDTO.builder()
                .id(idProveedor)
                .idEntidad(idEntidad)
                .codigoProveedor(row[1] != null ? row[1].toString() : null)
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
                .activo(false)
                .fechaRegistro(entRow[14] != null ? ((Timestamp) entRow[14]).toLocalDateTime() : null)
                .mensaje("Proveedor eliminado lógicamente con éxito")
                .build();
    }
    
    // ===============================================================
    // MÉTODOS AUXILIARES (SP Builder y Mapeos DTO)
    // ===============================================================
    
    /**
     * Construye y configura dinámicamente el Stored Procedure de creación o actualización.
     */
    private StoredProcedureQuery buildSP(ProveedorRequestDTO dto, String accion) {
        StoredProcedureQuery sp;
        
        if("CREATE".equalsIgnoreCase(accion)) {
            // SP de registro
            sp = entityManager.createStoredProcedureQuery("registrar_proveedor");
            // Registro de parámetros IN y OUT
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
            sp.registerStoredProcedureParameter("p_codigo_entidad", String.class, ParameterMode.OUT);
            sp.registerStoredProcedureParameter("p_codigo_proveedor", String.class, ParameterMode.OUT);
            sp.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
            
            // Asignación de parámetros
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
            sp.setParameter("p_representante", dto.getRepresentante());
            
        } else if("UPDATE".equalsIgnoreCase(accion)) {
            // SP de actualización
            sp = entityManager.createStoredProcedureQuery("actualizar_proveedor");
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
            sp.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
            
            // Asignación de parámetros
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
            sp.setParameter("p_representante", dto.getRepresentante());
            sp.setParameter("p_activo", dto.getActivo());
            
        } else {
            throw new RuntimeException("Acción de SP inválida: " + accion);
        }
        
        return sp;
    }
    
    /**
     * Mapea un resultado parcial de SP a un DTO de proveedor.
     */
    private ProveedorResponseDTO mapRowToDto(Object[] row) {
        if(row == null) return null;
        // Recupera datos y completa el DTO con información complementaria de la entidad
        Long idEntidad = row[3] != null ? ((Number) row[3]).longValue() : null;
        
        // Consulto la entidad
        Object[] entRow = (Object[]) entityManager.createNativeQuery(
                        "SELECT e.id, e.codigo, e.nombre, e.sexo, e.documento, e.id_tipo_persona_juridica, " +
                                "e.id_tipo_documento, e.correo, e.telefono, e.direccion, e.ciudad, e.distrito, " +
                                "e.representante, e.activo, e.fecha_registro FROM entidades e WHERE e.id = ?1")
                .setParameter(1, idEntidad)
                .getSingleResult();
        
        return ProveedorResponseDTO.builder()
                .id(row[0] != null ? ((Number) row[0]).longValue() : null)
                .codigoProveedor(row[1] != null ? row[1].toString() : null)
                .activo(row[2] != null ? ((Number) row[2]).intValue() == 1 : true)
                .idEntidad(row[3] != null ? ((Number) row[3]).longValue() : null)
                .nombre((String) row[5])
                .sexo(row[6] != null ? row[6].toString() : null)
                .documento((String) row[7])
                .idTipoPersonaJuridica(row[8] != null ? ((Number) row[8]).intValue() : null)
                .idTipoDocumento(row[9] != null ? ((Number) row[9]).intValue() : null)
                .correo((String) row[10])
                .telefono((String) row[11])
                .direccion((String) row[12])
                .ciudad((String) row[13])
                .distrito((String) row[14])
                .representante((String) row[15])
                .activo(row[16] != null ? ((Number) row[16]).intValue() == 1 : true)
                .fechaRegistro(row[17] != null ? ((Timestamp) row[17]).toLocalDateTime() : null)
                .mensaje("Operación exitosa")
                .build();
    }
    
    /**
     * Mapea una fila completa (JOIN) a un DTO de proveedor.
     */
    /**
     * Mapea una fila completa (JOIN) a un DTO de proveedor.
     */
    private ProveedorResponseDTO mapRowToFullDto(Object[] row) {
        if(row == null) return null;
        
        return ProveedorResponseDTO.builder()
                .id(row[0] != null ? ((Number) row[0]).longValue() : null)
                .codigoProveedor(row[1] != null ? row[1].toString() : null)
                .activo(row[2] != null ? ((Number) row[2]).intValue() == 1 : true)
                .idEntidad(row[3] != null ? ((Number) row[3]).longValue() : null)
                .nombre((String) row[5])
                .sexo(row[6] != null ? row[6].toString() : null)
                .documento((String) row[7])
                .idTipoPersonaJuridica(row[8] != null ? ((Number) row[8]).intValue() : null)
                .idTipoDocumento(row[9] != null ? ((Number) row[9]).intValue() : null)
                .correo((String) row[10])
                .telefono((String) row[11])
                .direccion((String) row[12])
                .ciudad((String) row[13])
                .distrito((String) row[14])
                .representante((String) row[15])
                .activo(row[16] != null ? ((Number) row[16]).intValue() == 1 : true)
                .fechaRegistro(row[17] != null ? ((Timestamp) row[17]).toLocalDateTime() : null)
                .mensaje("Operación exitosa")
                .build();
    }
    
}
